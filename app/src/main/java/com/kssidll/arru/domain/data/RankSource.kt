package com.kssidll.arru.domain.data

import java.util.Locale

interface RankSource: SortSource, FloatSource, IdentitySource {
    fun displayName(): String
    fun displayValue(locale: Locale): String
}