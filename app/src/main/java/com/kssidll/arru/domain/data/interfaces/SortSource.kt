package com.kssidll.arru.domain.data.interfaces

interface SortSource {
    fun sortValue(): Long
}

fun <E> List<E>.median(): Float where E : SortSource, E : FloatSource {
    if (isEmpty()) return 0f

    val sorted = sortedBy { it.sortValue() }
    val middle = sorted.size.div(2) - 1

    if (sorted.size % 2 == 1) return sorted[middle + 1].value()

    return (sorted[middle].value() + sorted[middle + 1].value()).div(2)
}
