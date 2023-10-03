package com.kssidll.arrugarq.domain.data

interface Rankable: Item {
    fun getDisplayName(): String
    fun getDisplayValue(): String
}