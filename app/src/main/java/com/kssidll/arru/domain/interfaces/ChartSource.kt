package com.kssidll.arru.domain.interfaces

import com.patrykandpatrick.vico.core.entry.ChartEntry

interface ChartSource: FloatSource, SortSource {
    fun chartEntry(x: Int): ChartEntry
    fun startAxisLabel(): String?
    fun topAxisLabel(): String?
    fun bottomAxisLabel(): String?
    fun endAxisLabel(): String?
}