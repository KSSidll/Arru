package com.kssidll.arru.domain.data

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
