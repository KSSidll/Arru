package com.kssidll.arru.domain.data

import androidx.collection.FloatFloatPair
import java.util.Locale

interface ChartSource: FloatSource, SortSource {
    fun chartEntry(x: Int): FloatFloatPair
    fun startAxisLabel(locale: Locale): String?
    fun topAxisLabel(locale: Locale): String?
    fun bottomAxisLabel(locale: Locale): String?
    fun endAxisLabel(locale: Locale): String?
}