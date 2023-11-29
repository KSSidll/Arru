package com.kssidll.arrugarq.domain.data

interface RankSource: SortSource, DoubleSource, IdentitySource {
    fun displayName(): String
    fun displayValue(): String
}