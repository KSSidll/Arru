package com.kssidll.arru.domain.interfaces

interface FuzzySearchSource {
    fun fuzzyScore(query: String): Int
}

fun <E> List<E>.fuzzySearchSort(query: String): List<E> where E: FuzzySearchSource {
    if (query.isBlank() || this.isEmpty()) return this

    return this.map {
        it to it.fuzzyScore(query)
    }
        .sortedByDescending { (_, score) -> score }
        .map { (element, _) -> element }
}