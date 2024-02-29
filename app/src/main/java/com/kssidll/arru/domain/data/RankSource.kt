package com.kssidll.arru.domain.data

interface RankSource: SortSource, FloatSource, IdentitySource {
    fun displayName(): String
    fun displayValue(): String
}