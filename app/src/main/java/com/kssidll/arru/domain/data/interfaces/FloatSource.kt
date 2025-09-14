package com.kssidll.arru.domain.data.interfaces

import androidx.collection.FloatFloatPair
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

interface FloatSource {
    fun value(): Float
}

fun <E> List<E>.avg(): Float where E : FloatSource {
    if (isEmpty()) return 0f

    var sum = 0f
    forEach { sum += it.value() }
    return sum / size
}

/**
 * assumes the list being ordered by time and creates a list representing the moving average in that
 * time
 *
 * @return list representing the moving average
 */
fun <E> List<E>.movingAverage(): List<Float> where E : FloatSource {
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
 * creates a list representing the moving average in time assumes the list is ordered by time
 *
 * @return list representing the [FloatFloatPair] of index (x) and moving average (y)
 */
fun <E> List<E>.movingAverageChartData(): List<FloatFloatPair> where E : FloatSource {
    if (isEmpty()) return emptyList()

    return movingAverage().mapIndexed { index, median -> FloatFloatPair(index.toFloat(), median) }
}

/**
 * Runs a transaction committing [FloatSource] as moving average to the
 * [CartesianChartModelProducer]
 */
suspend fun <E> CartesianChartModelProducer.runMovingAverageChartDataTransaction(data: List<E>)
    where E : FloatSource {
    val newAverageChartData = data.movingAverageChartData().toMutableList()

    if (newAverageChartData.size == 1) {
        newAverageChartData.add(
            FloatFloatPair(
                newAverageChartData.first().first + 1.0f,
                newAverageChartData.first().second,
            )
        )
    }

    if (newAverageChartData.isNotEmpty()) {
        runTransaction {
            lineSeries {
                series(
                    x = newAverageChartData.map { it.first },
                    y = newAverageChartData.map { it.second },
                )
            }
        }
    }
}

/**
 * assumes the list being ordered by time and creates a list representing the moving total in that
 * time
 *
 * @return list representing the moving total
 */
fun <E> List<E>.movingTotal(): List<Float> where E : FloatSource {
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
 * creates a list representing the moving total in time assumes the list is ordered by time
 *
 * @return list representing the [FloatFloatPair] of index (x) and moving total (y)
 */
fun <E> List<E>.movingTotalChartData(): List<FloatFloatPair> where E : FloatSource {
    if (isEmpty()) return emptyList()

    return movingTotal().mapIndexed { index, median -> FloatFloatPair(index.toFloat(), median) }
}

/**
 * Runs a transaction committing [FloatSource] as moving total to the [CartesianChartModelProducer]
 */
suspend fun <E> CartesianChartModelProducer.runMovingTotalChartDataTransaction(data: List<E>)
    where E : FloatSource {
    val newTotalChartData = data.movingTotalChartData().toMutableList()

    if (newTotalChartData.size == 1) {
        newTotalChartData.add(
            FloatFloatPair(newTotalChartData.first().first + 1.0f, newTotalChartData.first().second)
        )
    }

    if (newTotalChartData.isNotEmpty()) {
        runTransaction {
            lineSeries {
                series(
                    x = newTotalChartData.map { it.first },
                    y = newTotalChartData.map { it.second },
                )
            }
        }
    }
}

/**
 * assumes the list being ordered by time and creates a list representing the moving median in that
 * time
 *
 * @return list representing the moving median
 */
fun <E> List<E>.movingMedian(): List<Float> where E : SortSource, E : FloatSource {
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
 * creates a list representing the moving median in time assumes the list is ordered by time
 *
 * @return list representing the [FloatFloatPair] of index (x) and moving median (y)
 */
fun <E> List<E>.movingMedianChartData(): List<FloatFloatPair>
    where E : SortSource, E : FloatSource {
    if (isEmpty()) return emptyList()

    return movingMedian().mapIndexed { index, median -> FloatFloatPair(index.toFloat(), median) }
}

/**
 * Runs a transaction committing [SortSource] [FloatSource] as moving average to the
 * [CartesianChartModelProducer]
 */
suspend fun <E> CartesianChartModelProducer.runMovingMedianChartDataTransaction(data: List<E>)
    where E : SortSource, E : FloatSource {
    val newMedianChartData = data.movingMedianChartData().toMutableList()

    if (newMedianChartData.size == 1) {
        newMedianChartData.add(
            FloatFloatPair(
                newMedianChartData.first().first + 1.0f,
                newMedianChartData.first().second,
            )
        )
    }

    if (newMedianChartData.isNotEmpty()) {
        runTransaction {
            lineSeries {
                series(
                    x = newMedianChartData.map { it.first },
                    y = newMedianChartData.map { it.second },
                )
            }
        }
    }
}
