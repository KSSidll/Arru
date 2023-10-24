package com.kssidll.arrugarq.domain.data

interface Rankable: Sortable, FloatProvider, Identifiable {
    fun displayName(): String
    fun displayValue(): String
}