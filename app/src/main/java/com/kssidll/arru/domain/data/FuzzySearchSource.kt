package com.kssidll.arru.domain.data

interface FuzzySearchSource {
    fun fuzzyScore(query: String): Int
}

fun <E> List<E>.fuzzySearchSort(query: String): List<E> where E: FuzzySearchSource {
    if (query.isBlank()) return this

    return fuzzySearchSort { it.fuzzyScore(query) }
}

fun <E> List<E>.fuzzySearchSort(
    calculateScore: (E) -> Int
): List<E> {
    if (this.isEmpty()) return this

    return this.map {
        it to calculateScore(it)
    }
        .sortedByDescending { (_, score) -> score }
        .map { (element, _) -> element }
}