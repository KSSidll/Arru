package com.kssidll.arrugarq.domain.data

interface FuzzySearchable {
    fun getFuzzyScore(query: String): Int
}

fun <E> List<E>.fuzzySearchSort(query: String): List<E> where E: FuzzySearchable {
    return this.map {
        it to it.getFuzzyScore(query)
    }
        .sortedByDescending { (_, score) -> score }
        .map { (element, _) -> element }
}