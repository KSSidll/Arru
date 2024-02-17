package com.kssidll.arru.domain.data

import com.patrykandpatrick.vico.core.entry.*

interface ChartSource: DoubleSource, SortSource {
    fun chartEntry(x: Int): ChartEntry
    fun startAxisLabel(): String?
    fun topAxisLabel(): String?
    fun bottomAxisLabel(): String?
    fun endAxisLabel(): String?
}