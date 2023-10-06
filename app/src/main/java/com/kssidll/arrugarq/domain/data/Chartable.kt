package com.kssidll.arrugarq.domain.data

import com.patrykandpatrick.vico.core.entry.*

interface Chartable: FloatProvider, Sortable {
    fun chartEntry(x: Int): ChartEntry
    fun startAxisLabel(): String?
    fun topAxisLabel(): String?
    fun bottomAxisLabel(): String?
    fun endAxisLabel(): String?
}