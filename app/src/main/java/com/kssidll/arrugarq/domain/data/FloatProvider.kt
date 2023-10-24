package com.kssidll.arrugarq.domain.data

interface FloatProvider {
    fun value(): Float
}

fun <E> List<E>.avg(): Float where E: FloatProvider {
    if (isEmpty()) return 0F

    var sum = 0F
    forEach {
        sum += it.value()
    }
    return sum / size
}
