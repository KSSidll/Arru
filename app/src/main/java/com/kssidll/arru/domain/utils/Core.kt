package com.kssidll.arru.domain.utils

fun Int?.orZero(): Int {
    return this ?: 0
}