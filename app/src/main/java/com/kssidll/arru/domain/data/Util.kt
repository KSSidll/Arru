package com.kssidll.arru.domain.data

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

/**
 * Checks whether the [LazyPagingItems] reported loaded state but is empty
 *
 * @return true if [LazyPagingItems] reported loaded state but has no items, false otherwise
 */
fun <T> LazyPagingItems<T>.loadedEmpty(): Boolean where T : Any {
    return loadState.refresh is LoadState.NotLoading && itemCount == 0
}

fun <T> emptyImmutableList(): ImmutableList<T> {
    return emptyList<T>().toImmutableList()
}

fun <T> ImmutableList<T>?.orEmpty(): ImmutableList<T> {
    return this ?: emptyImmutableList()
}

fun <T> emptyImmutableSet(): ImmutableSet<T> {
    return emptySet<T>().toImmutableSet()
}

fun <T> ImmutableSet<T>?.orEmpty(): ImmutableSet<T> {
    return this ?: emptyImmutableSet()
}
