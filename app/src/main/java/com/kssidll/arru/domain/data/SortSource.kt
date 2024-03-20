package com.kssidll.arru.domain.data

import com.patrykandpatrick.vico.core.entry.*

interface SortSource {
    fun sortValue(): Long
}

fun <E> List<E>.median(): Float where E: SortSource, E: FloatSource {
    if (isEmpty()) return 0f

    val sorted = sortedBy { it.sortValue() }
    val middle = sorted.size.div(2) - 1

    if (sorted.size % 2 == 1) return sorted[middle + 1].value()

    return (sorted[middle].value() + sorted[middle + 1].value()).div(2)
}

/**
 * assumes the list being ordered by time and creates a list representing the moving median in that time
 * @return list representing the moving median
 */
fun <E> List<E>.movingMedian(): List<Float> where E: SortSource, E: FloatSource {
    if (isEmpty()) return emptyList()

    val results = mutableListOf<Float>()
    val calculationList = mutableListOf<E>()
    forEach { item ->
        calculationList.add(item)

        val sorted = calculationList.sortedBy { it.sortValue() }
        val middle = sorted.size.div(2) - 1

        if (sorted.size % 2 == 1) results.add(sorted[middle + 1].value())
        else results.add((sorted[middle].value() + sorted[middle + 1].value()).div(2))
    }

    return results

}

/**
 * assumes the list being ordered by time and creates a list representing the moving median in that time
 * @return list representing the moving median
 */
fun <E> List<E>.movingMedianChartData(): List<ChartEntry> where E: SortSource, E: FloatSource {
    if (isEmpty()) return emptyList()

    return movingMedian().mapIndexed { index, median ->
        FloatEntry(
            index.toFloat(),
            median
        )
    }
}