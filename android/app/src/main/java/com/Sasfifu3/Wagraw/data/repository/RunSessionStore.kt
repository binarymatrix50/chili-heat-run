package com.Sasfifu3.Wagraw.data.repository

import com.Sasfifu3.Wagraw.domain.model.RouteStop
import com.Sasfifu3.Wagraw.domain.model.RunResult

class RunSessionStore {

    var route: List<RouteStop> = emptyList()
        private set

    var attempt: Int = 0
        private set

    var lastResult: RunResult? = null
        private set

    fun setRoute(stops: List<RouteStop>) {
        route = stops
    }

    fun nextAttempt() {
        attempt += 1
    }

    fun publish(result: RunResult) {
        lastResult = result
    }
}
