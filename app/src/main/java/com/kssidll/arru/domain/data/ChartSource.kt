package com.kssidll.arru.domain.data

import com.patrykandpatrick.vico.core.entry.ChartEntry
import java.util.Locale

interface ChartSource: FloatSource, SortSource {
    fun chartEntry(x: Int): ChartEntry
    fun startAxisLabel(locale: Locale): String?
    fun topAxisLabel(locale: Locale): String?
    fun bottomAxisLabel(locale: Locale): String?
    fun endAxisLabel(locale: Locale): String?
}