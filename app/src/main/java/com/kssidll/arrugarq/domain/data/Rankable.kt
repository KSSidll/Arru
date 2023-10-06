package com.kssidll.arrugarq.domain.data

interface Rankable: Sortable, FloatProvider, Identifiable {
    fun getDisplayName(): String
    fun getDisplayValue(): String
}