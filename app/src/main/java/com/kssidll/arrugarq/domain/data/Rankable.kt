package com.kssidll.arrugarq.domain.data

interface Rankable {
    fun getDisplayName(): String
    fun getDisplayValue(): String
    fun getValue(): Long
}