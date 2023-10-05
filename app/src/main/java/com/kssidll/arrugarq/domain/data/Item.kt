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

fun <E> List<E>.median(): Float where E: Item {
    if (isEmpty()) return 0F

    val sorted = this.sortedBy { it.getSortValue() }
    val middle = sorted.size.div(2) - 1

    if (sorted.size % 2 == 1) return sorted[middle + 1].getValue()

    return (sorted[middle].getValue() + sorted[middle + 1].getValue()).div(2)
}
