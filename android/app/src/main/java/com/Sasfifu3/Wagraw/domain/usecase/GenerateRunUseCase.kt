package com.Sasfifu3.Wagraw.domain.usecase

import com.Sasfifu3.Wagraw.core.config.GameConfig
import com.Sasfifu3.Wagraw.domain.model.Ingredient
import com.Sasfifu3.Wagraw.domain.model.RouteStop
import com.Sasfifu3.Wagraw.domain.model.RunConfig
import com.Sasfifu3.Wagraw.domain.model.RunPlan
import com.Sasfifu3.Wagraw.domain.model.StopPlan
import kotlin.random.Random

class GenerateRunUseCase {

    operator fun invoke(
        route: List<RouteStop>,
        routeIndex: Int,
        attempt: Int,
        config: RunConfig
    ): RunPlan {
        val seed = (routeIndex.toLong() * 7919L) xor (attempt.toLong() * 104729L)
        repeat(GameConfig.GENERATION_ATTEMPTS) { pass ->
            val random = Random(seed + pass)
            val stops = route.map { buildStop(it, random, config) }
            if (stops.all { isSolvable(it, config.dishLimit) }) {
                return RunPlan(seed, stops)
            }
        }
        val random = Random(seed)
        return RunPlan(seed, route.map { fallbackStop(it, random, config) })
    }

    fun offerForSlot(
        pool: List<RouteStop>,
        taken: Set<Int>,
        seed: Long,
        slot: Int,
        reroll: Int
    ): List<RouteStop> {
        val candidates = pool.filterNot { taken.contains(it.id) }
        if (candidates.size <= 2) return candidates
        val random = Random(seed + slot * 977L + reroll * 131L)
        return candidates.shuffled(random).take(2).sortedBy { it.difficulty }
    }

    private fun buildStop(stop: RouteStop, random: Random, config: RunConfig): StopPlan {
        val count = stop.customers + stop.modifier.extraCustomers
        val low = (stop.minDemand + stop.modifier.demandBonus).coerceIn(0, GameConfig.MAX_DISH_HEAT)
        val high = (stop.maxDemand + stop.modifier.demandBonus).coerceIn(low, GameConfig.MAX_DISH_HEAT)
        val stock = Ingredient.entries.associateWith {
            (2 + random.nextInt(3) + config.extraStock - stop.modifier.stockPenalty).coerceAtLeast(1)
        }
        val demands = List(count) { low + random.nextInt(high - low + 1) }
        return StopPlan(stop, demands, stock, patienceOf(stop, config))
    }

    private fun fallbackStop(stop: RouteStop, random: Random, config: RunConfig): StopPlan {
        val count = stop.customers + stop.modifier.extraCustomers
        val stock = Ingredient.entries.associateWith { 4 + config.extraStock }
        val demands = List(count) { 1 + random.nextInt(4) }
        return StopPlan(stop, demands, stock, patienceOf(stop, config))
    }

    private fun patienceOf(stop: RouteStop, config: RunConfig): Long =
        (GameConfig.CUSTOMER_PATIENCE_MS * stop.modifier.patienceFactor * config.patienceFactor).toLong()

    private fun isSolvable(plan: StopPlan, dishLimit: Int): Boolean {
        val reachable = reachableHeats(plan.stock, dishLimit)
        return plan.demands.all { reachable.contains(it) }
    }

    private fun reachableHeats(stock: Map<Ingredient, Int>, dishLimit: Int): Set<Int> {
        val items = Ingredient.entries.toList()
        val sums = mutableSetOf<Int>()
        fun walk(index: Int, used: Int, total: Int) {
            if (index == items.size) {
                if (used > 0) sums.add(total.coerceIn(0, GameConfig.MAX_DISH_HEAT))
                return
            }
            val limit = minOf(stock[items[index]] ?: 0, dishLimit - used)
            for (count in 0..limit) {
                walk(index + 1, used + count, total + items[index].heat * count)
            }
        }
        walk(0, 0, 0)
        return sums
    }
}
