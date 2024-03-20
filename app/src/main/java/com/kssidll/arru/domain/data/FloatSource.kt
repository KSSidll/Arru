package com.kssidll.arru.domain.data

import com.patrykandpatrick.vico.core.entry.*

interface FloatSource {
    fun value(): Float
}

fun <E> List<E>.avg(): Float where E: FloatSource {
    if (isEmpty()) return 0f

    var sum = 0f
    forEach {
        sum += it.value()
    }
    return sum / size
}

/**
 * assumes the list being ordered by time and creates a list representing the moving average in that time
 * @return list representing the moving average
 */
fun <E> List<E>.movingAverage(): List<Float> where E: FloatSource {
    if (isEmpty()) return emptyList()

    val results = mutableListOf<Float>()
    var sum = 0f
    forEach { item ->
        sum += item.value()
        results.add(sum / (results.size + 1))
    }

    return results

}

/**
 * assumes the list being ordered by time and creates a list representing the moving average in that time
 * @return list representing the moving average
 */
fun <E> List<E>.movingAverageChartData(): List<ChartEntry> where E: FloatSource {
    if (isEmpty()) return emptyList()

    return movingAverage().mapIndexed { index, median ->
        FloatEntry(
            index.toFloat(),
            median
        )
    }
}

/**
 * assumes the list being ordered by time and creates a list representing the moving total in that time
 * @return list representing the moving total
 */
fun <E> List<E>.movingTotal(): List<Float> where E: FloatSource {
    if (isEmpty()) return emptyList()

    val results = mutableListOf<Float>()
    var sum = 0f
    forEach { item ->
        sum += item.value()
        results.add(sum)
    }

    return results

}

/**
 * assumes the list being ordered by time and creates a list representing the moving total in that time
 * @return list representing the moving total
 */
fun <E> List<E>.movingTotalChartData(): List<ChartEntry> where E: FloatSource {
    if (isEmpty()) return emptyList()

    return movingTotal().mapIndexed { index, median ->
        FloatEntry(
            index.toFloat(),
            median
        )
    }
}
