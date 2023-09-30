package com.kssidll.arrugarq.domain.data

import com.patrykandpatrick.vico.core.entry.*

interface Chartable {
    fun chartEntry(x: Int): ChartEntry
    fun startAxisLabel(): String = String()
    fun topAxisLabel(): String = String()
    fun bottomAxisLabel(): String = String()
    fun endAxisLabel(): String = String()
}