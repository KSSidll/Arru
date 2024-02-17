package com.kssidll.arru.domain.data

interface RankSource: SortSource, DoubleSource, IdentitySource {
    fun displayName(): String
    fun displayValue(): String
}