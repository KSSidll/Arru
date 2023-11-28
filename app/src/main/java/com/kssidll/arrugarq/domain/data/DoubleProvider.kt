package com.kssidll.arrugarq.domain.data

interface DoubleProvider {
    fun value(): Double
}

fun <E> List<E>.avg(): Double where E: DoubleProvider {
    if (isEmpty()) return 0.0

    var sum = 0.0
    forEach {
        sum += it.value()
    }
    return sum / size
}
