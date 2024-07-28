package com.kssidll.arru.domain.interfaces

interface RankSource: SortSource, FloatSource, IdentitySource {
    fun displayName(): String
    fun displayValue(): String
}