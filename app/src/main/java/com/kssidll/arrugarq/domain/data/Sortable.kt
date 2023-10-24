package com.kssidll.arrugarq.domain.data

interface Sortable {
    fun sortValue(): Long
}

fun <E> List<E>.median(): Float where E: Sortable, E: FloatProvider {
    if (isEmpty()) return 0F

    val sorted = this.sortedBy { it.sortValue() }
    val middle = sorted.size.div(2) - 1

    if (sorted.size % 2 == 1) return sorted[middle + 1].value()

    return (sorted[middle].value() + sorted[middle + 1].value()).div(2)
}

