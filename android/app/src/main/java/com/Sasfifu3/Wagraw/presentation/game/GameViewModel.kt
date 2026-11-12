package com.Sasfifu3.Wagraw.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Sasfifu3.Wagraw.core.config.GameConfig
import com.Sasfifu3.Wagraw.data.repository.RunSessionStore
import com.Sasfifu3.Wagraw.data.sample.SampleData
import com.Sasfifu3.Wagraw.domain.model.Customer
import com.Sasfifu3.Wagraw.domain.model.Ingredient
import com.Sasfifu3.Wagraw.domain.model.Recipe
import com.Sasfifu3.Wagraw.domain.model.RunConfig
import com.Sasfifu3.Wagraw.domain.model.RunEndReason
import com.Sasfifu3.Wagraw.domain.model.RunPlan
import com.Sasfifu3.Wagraw.domain.model.RunResult
import com.Sasfifu3.Wagraw.domain.model.ServeOutcome
import com.Sasfifu3.Wagraw.domain.repository.ProgressRepository
import com.Sasfifu3.Wagraw.domain.repository.SettingsRepository
import com.Sasfifu3.Wagraw.domain.usecase.ApplyUpgradesUseCase
import com.Sasfifu3.Wagraw.domain.usecase.GenerateRunUseCase
import com.Sasfifu3.Wagraw.domain.usecase.ResolveServeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val runSession: RunSessionStore,
    private val generateRun: GenerateRunUseCase,
    private val resolveServe: ResolveServeUseCase,
    private val applyUpgrades: ApplyUpgradesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = _events.receiveAsFlow()

    private var activeRunKey = -1
    private var loopJob: Job? = null
    private var runPlan: RunPlan? = null
    private var runConfig: RunConfig = RunConfig(
        ventCharges = GameConfig.VENT_CHARGES,
        ventAmount = GameConfig.VENT_AMOUNT,
        dishLimit = GameConfig.DISH_MAX_INGREDIENTS,
        patienceFactor = 1.0f,
        coinFactor = 1.0f,
        extraStock = 0,
        assisted = false
    )

    private val stock = mutableMapOf<Ingredient, Int>()
    private val pendingDemands = mutableListOf<Int>()
    private val queue = mutableListOf<Customer>()
    private val plate = mutableListOf<Ingredient>()

    private var signatureRecipe: Recipe? = null
    private var spawnTimer = 0L
    private var nextCustomerId = 0
    private var stopIndex = 0
    private var stallHeat = 0
    private var streak = 0
    private var bestStreak = 0
    private var perfectServes = 0
    private var coinsEarned = 0
    private var spiceEarned = 0
    private var ventCharges = GameConfig.VENT_CHARGES
    private var skipTokens = GameConfig.SKIP_TOKENS_PER_STOP
    private var lastHeatFromWalkout = false
    private var runOver = false

    fun enter(runKey: Int) {
        if (runKey == activeRunKey) {
            val state = _uiState.value
            if (state.phase == GamePhase.PLAYING && !state.paused) {
                startLoop()
            }
            return
        }
        activeRunKey = runKey
        startRun()
    }

    fun leave() {
        loopJob?.cancel()
        loopJob = null
    }

    override fun onCleared() {
        super.onCleared()
        loopJob?.cancel()
    }

    private fun startRun() {
        loopJob?.cancel()
        loopJob = null
        runOver = false
        val progress = progressRepository.load()
        val settings = settingsRepository.load()
        runConfig = applyUpgrades(progress.ownedUpgrades, settings)
        signatureRecipe = SampleData.recipeById(progress.equippedRecipe)
        runSession.nextAttempt()
        val route = runSession.route.ifEmpty { SampleData.defaultRoute }
        runPlan = generateRun(route, progress.stats.runs + 1, runSession.attempt, runConfig)
        stopIndex = 0
        stallHeat = 0
        streak = 0
        bestStreak = 0
        perfectServes = 0
        coinsEarned = 0
        spiceEarned = 0
        ventCharges = runConfig.ventCharges
        lastHeatFromWalkout = false
        _uiState.value = GameUiState(
            dishLimit = runConfig.dishLimit,
            ventCharges = ventCharges,
            ventMax = runConfig.ventCharges,
            heatNumbers = settings.heatNumbers,
            leftHanded = settings.leftHandedTray,
            totalStops = route.size,
            signatureNameRes = signatureRecipe?.nameRes
        )
        loadStop(0)
    }

    private fun loadStop(index: Int) {
        val plan = runPlan ?: return
        val stopPlan = plan.stops.getOrNull(index) ?: return
        stock.clear()
        stock.putAll(stopPlan.stock)
        pendingDemands.clear()
        pendingDemands.addAll(stopPlan.demands)
        queue.clear()
        plate.clear()
        skipTokens = GameConfig.SKIP_TOKENS_PER_STOP
        spawnTimer = 0L
        _uiState.update {
            it.copy(
                ready = true,
                phase = GamePhase.BRIEF,
                stopIndex = index,
                totalStops = plan.stops.size,
                stopNameRes = stopPlan.stop.nameRes,
                stopModifierRes = stopPlan.stop.modifierRes,
                stopGuests = stopPlan.demands.size,
                stopMinDemand = stopPlan.demands.minOrNull() ?: 0,
                stopMaxDemand = stopPlan.demands.maxOrNull() ?: 0,
                stopCoins = 0,
                lastOutcome = null,
                skipTokens = skipTokens
            )
        }
        syncBoard()
    }

    fun beginStop() {
        if (_uiState.value.phase != GamePhase.BRIEF) return
        spawnCustomer()
        spawnTimer = 0L
        _uiState.update { it.copy(phase = GamePhase.PLAYING, paused = false) }
        syncBoard()
        startLoop()
    }

    fun pause() {
        if (_uiState.value.phase != GamePhase.PLAYING) return
        loopJob?.cancel()
        loopJob = null
        _uiState.update { it.copy(paused = true) }
    }

    fun resume() {
        if (!_uiState.value.paused) return
        _uiState.update { it.copy(paused = false) }
        startLoop()
    }

    fun addIngredient(ingredient: Ingredient) {
        val state = _uiState.value
        if (state.phase != GamePhase.PLAYING || state.paused) return
        if (plate.size >= runConfig.dishLimit) return
        val available = stock[ingredient] ?: 0
        if (available <= 0) return
        stock[ingredient] = available - 1
        plate.add(ingredient)
        syncBoard()
    }

    fun clearPlate() {
        if (plate.isEmpty()) return
        plate.forEach { ingredient -> stock[ingredient] = (stock[ingredient] ?: 0) + 1 }
        plate.clear()
        syncBoard()
    }

    fun vent() {
        val state = _uiState.value
        if (state.phase != GamePhase.PLAYING || state.paused || ventCharges <= 0) return
        ventCharges -= 1
        stallHeat = (stallHeat - runConfig.ventAmount).coerceAtLeast(0)
        syncBoard()
    }

    fun skipCustomer() {
        val state = _uiState.value
        if (state.phase != GamePhase.PLAYING || state.paused) return
        if (skipTokens <= 0 || queue.size < 2) return
        skipTokens -= 1
        val front = queue.removeAt(0)
        queue.add(front.copy(patienceLeftMs = front.patienceTotalMs))
        syncBoard()
    }

    fun serve() {
        val state = _uiState.value
        if (state.phase != GamePhase.PLAYING || state.paused) return
        if (plate.isEmpty() || queue.isEmpty()) return
        val customer = queue[0]
        val dishHeat = plate.sumOf { it.heat }.coerceIn(0, GameConfig.MAX_DISH_HEAT)
        val signature = isSignature(customer.demand)
        val stopFactor = runPlan?.stops?.getOrNull(stopIndex)?.stop?.modifier?.coinFactor ?: 1.0f
        val result = resolveServe(
            dishHeat = dishHeat,
            demand = customer.demand,
            streak = streak,
            coinFactor = runConfig.coinFactor * stopFactor,
            signature = signature
        )
        loopJob?.cancel()
        loopJob = null
        _uiState.update { it.copy(phase = GamePhase.SERVING, lastOutcome = result.outcome) }
        viewModelScope.launch {
            delay(GameConfig.SERVE_ANIM_MS)
            queue.removeAt(0)
            plate.clear()
            coinsEarned += result.coins
            spiceEarned += result.spice
            streak = result.streak
            if (streak > bestStreak) bestStreak = streak
            if (result.outcome == ServeOutcome.PERFECT) perfectServes += 1
            if (streak > 0 && streak % GameConfig.VENT_STREAK_RECHARGE == 0 &&
                ventCharges < runConfig.ventCharges
            ) {
                ventCharges += 1
            }
            stallHeat = (stallHeat + result.heatDelta).coerceIn(0, GameConfig.STALL_HEAT_MAX)
            lastHeatFromWalkout = false
            _uiState.update { it.copy(stopCoins = it.stopCoins + result.coins) }
            when {
                stallHeat >= GameConfig.STALL_HEAT_MAX -> finishRun(RunEndReason.OVERHEAT)
                queue.isEmpty() && pendingDemands.isEmpty() -> clearStop()
                else -> {
                    _uiState.update { it.copy(phase = GamePhase.PLAYING) }
                    syncBoard()
                    startLoop()
                }
            }
        }
    }

    private fun isSignature(demand: Int): Boolean {
        val recipe = signatureRecipe ?: return false
        if (plate.size != recipe.ingredients.size) return false
        if (plate.sortedBy { it.ordinal } != recipe.ingredients.sortedBy { it.ordinal }) return false
        return demand == recipe.heat
    }

    private fun startLoop() {
        if (runOver) return
        if (loopJob?.isActive == true) return
        loopJob = viewModelScope.launch {
            var previous = System.currentTimeMillis()
            while (isActive) {
                delay(GameConfig.TICK_MS)
                val now = System.currentTimeMillis()
                val elapsed = (now - previous).coerceAtMost(200L)
                previous = now
                tick(elapsed)
            }
        }
    }

    private fun tick(elapsed: Long) {
        if (_uiState.value.phase != GamePhase.PLAYING) return
        spawnTimer += elapsed
        if (queue.size < GameConfig.QUEUE_MAX && pendingDemands.isNotEmpty() &&
            spawnTimer >= GameConfig.CUSTOMER_SPAWN_GAP_MS
        ) {
            spawnCustomer()
            spawnTimer = 0L
        }
        var walkouts = 0
        var index = 0
        while (index < queue.size) {
            val updated = queue[index].copy(patienceLeftMs = queue[index].patienceLeftMs - elapsed)
            if (updated.patienceLeftMs <= 0L) {
                queue.removeAt(index)
                walkouts += 1
            } else {
                queue[index] = updated
                index += 1
            }
        }
        if (walkouts > 0) {
            streak = 0
            stallHeat = (stallHeat + walkouts * GameConfig.WALKOUT_HEAT)
                .coerceIn(0, GameConfig.STALL_HEAT_MAX)
            lastHeatFromWalkout = true
        }
        syncBoard()
        if (stallHeat >= GameConfig.STALL_HEAT_MAX) {
            finishRun(if (lastHeatFromWalkout) RunEndReason.WALKOUTS else RunEndReason.OVERHEAT)
            return
        }
        if (queue.isEmpty() && pendingDemands.isEmpty()) {
            clearStop()
        }
    }

    private fun spawnCustomer() {
        if (pendingDemands.isEmpty()) return
        val plan = runPlan?.stops?.getOrNull(stopIndex) ?: return
        val demand = pendingDemands.removeAt(0)
        nextCustomerId += 1
        queue.add(
            Customer(
                id = nextCustomerId,
                demand = demand,
                patienceLeftMs = plan.patienceMs,
                patienceTotalMs = plan.patienceMs
            )
        )
    }

    private fun clearStop() {
        if (runOver) return
        loopJob?.cancel()
        loopJob = null
        spiceEarned += GameConfig.SPICE_PER_STOP_CLEARED
        _uiState.update { it.copy(phase = GamePhase.STOP_CLEARED) }
        syncBoard()
        viewModelScope.launch {
            delay(GameConfig.STOP_CLEARED_ANIM_MS)
            val plan = runPlan
            val next = stopIndex + 1
            if (plan == null || next >= plan.stops.size) {
                finishRun(RunEndReason.ROUTE_SERVED)
            } else {
                stopIndex = next
                loadStop(next)
            }
        }
    }

    private fun finishRun(reason: RunEndReason) {
        if (runOver) return
        runOver = true
        loopJob?.cancel()
        loopJob = null
        val stopsCleared = if (reason == RunEndReason.ROUTE_SERVED) {
            runPlan?.stops?.size ?: stopIndex
        } else {
            stopIndex
        }
        val progress = progressRepository.load()
        val previousBest = progressRepository.bestScore()
        val newBest = coinsEarned > previousBest
        if (newBest) progressRepository.saveBestScore(coinsEarned)
        val stats = progress.stats.copy(
            runs = progress.stats.runs + 1,
            bestStops = maxOf(progress.stats.bestStops, stopsCleared),
            perfectServes = progress.stats.perfectServes + perfectServes,
            totalCoins = progress.stats.totalCoins + coinsEarned,
            bestStreak = maxOf(progress.stats.bestStreak, bestStreak),
            routesCleared = progress.stats.routesCleared +
                if (reason == RunEndReason.ROUTE_SERVED) 1 else 0,
            lastStops = stopsCleared,
            lastCoins = coinsEarned
        )
        val updatedSpice = progress.spiceTokens + spiceEarned
        progressRepository.save(
            progress.copy(
                coins = progress.coins + coinsEarned,
                spiceTokens = updatedSpice,
                stats = stats
            )
        )
        val nextRecipe = SampleData.recipes
            .filterNot { progress.unlockedRecipes.contains(it.id) }
            .filter { it.cost <= updatedSpice }
            .minByOrNull { it.cost }
        runSession.publish(
            RunResult(
                reason = reason,
                stopsCleared = stopsCleared,
                totalStops = runPlan?.stops?.size ?: GameConfig.ROUTE_STOPS,
                stopNameRes = runPlan?.stops?.getOrNull(stopIndex)?.stop?.nameRes
                    ?: _uiState.value.stopNameRes,
                coinsEarned = coinsEarned,
                perfectServes = perfectServes,
                bestStreak = bestStreak,
                spiceEarned = spiceEarned,
                assisted = runConfig.assisted,
                newBest = newBest,
                nextRecipeNameRes = nextRecipe?.nameRes
            )
        )
        _uiState.update {
            it.copy(
                phase = GamePhase.FINISHING,
                paused = false,
                stallHeat = stallHeat,
                overheated = reason != RunEndReason.ROUTE_SERVED
            )
        }
        viewModelScope.launch {
            delay(GameConfig.FIRE_ANIM_MS)
            _events.send(GameEvent.Finished)
        }
    }

    private fun syncBoard() {
        val front = queue.firstOrNull()?.demand
        val dishHeat = plate.sumOf { it.heat }.coerceIn(0, GameConfig.MAX_DISH_HEAT)
        _uiState.update { state ->
            state.copy(
                stallHeat = stallHeat,
                coins = coinsEarned,
                streak = streak,
                perfectServes = perfectServes,
                queue = queue.map { QueueCardUi(it.id, it.demand, it.patienceFraction) },
                tray = Ingredient.entries.map { ingredient ->
                    TraySlotUi(
                        ingredient = ingredient,
                        labelRes = SampleData.labelOf(ingredient),
                        artRes = SampleData.artOf(ingredient),
                        heat = ingredient.heat,
                        stock = stock[ingredient] ?: 0
                    )
                },
                plate = plate.map {
                    PlateChipUi(it, SampleData.labelOf(it), SampleData.artOf(it))
                },
                dishHeat = dishHeat,
                dishLimit = runConfig.dishLimit,
                frontDemand = front,
                ventCharges = ventCharges,
                ventMax = runConfig.ventCharges,
                skipTokens = skipTokens,
                signatureReady = front != null && isSignature(front)
            )
        }
    }
}
