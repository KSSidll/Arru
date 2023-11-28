package com.kssidll.arrugarq.domain.data

interface Rankable: Sortable, DoubleProvider, Identifiable {
    fun displayName(): String
    fun displayValue(): String
}