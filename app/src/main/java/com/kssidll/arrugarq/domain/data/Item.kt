package com.kssidll.arrugarq.domain.data

interface Item {
    fun getValue(): Float
    fun getSortValue(): Long
}

fun <E> List<E>.avg(): Float where E: Item {
    if (isEmpty()) return 0F

    var sum = 0F
    forEach {
        sum += it.getValue()
    }
    return sum / size
}

