package com.kssidll.arrugarq.data.data

interface IFuzzySearchable {
    fun getFuzzyScore(query: String): Int
}

fun <E> List<E>.fuzzySearchSort(query: String) : List<E> where E : IFuzzySearchable {
    return this.map {
        it to it.getFuzzyScore(query)
    }
        .sortedByDescending { (_, score) -> score }
        .map { (element, _) -> element}
}