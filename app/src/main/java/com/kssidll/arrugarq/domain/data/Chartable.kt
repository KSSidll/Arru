package com.kssidll.arrugarq.domain.data

import com.patrykandpatrick.vico.core.entry.*

interface Chartable: DoubleProvider, Sortable {
    fun chartEntry(x: Int): ChartEntry
    fun startAxisLabel(): String?
    fun topAxisLabel(): String?
    fun bottomAxisLabel(): String?
    fun endAxisLabel(): String?
}