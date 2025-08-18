package com.kssidll.arru.domain.data.interfaces

import androidx.collection.FloatFloatPair
import java.util.Locale

interface ChartSource : FloatSource, SortSource {
    fun dataOrder(): Long

    fun chartEntry(): FloatFloatPair

    fun chartEntry(x: Int): FloatFloatPair

    fun startAxisLabel(locale: Locale): String?

    fun topAxisLabel(locale: Locale): String?

    fun endAxisLabel(locale: Locale): String?

    fun bottomAxisLabel(locale: Locale): String?
}
