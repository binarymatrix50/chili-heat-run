package com.Sasfifu3.Wagraw.domain.repository

import com.Sasfifu3.Wagraw.domain.model.PlayerProgress

interface ProgressRepository {
    fun load(): PlayerProgress
    fun save(progress: PlayerProgress)
    fun bestScore(): Int
    fun saveBestScore(score: Int)
    fun clear()
}
