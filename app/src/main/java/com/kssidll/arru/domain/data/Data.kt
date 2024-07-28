package com.kssidll.arru.domain.data

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

/**
 * A generic abstraction for repository data with loaded and loading states
 */
sealed class Data<T> {
    /**
     * Signifies loaded state with some contained data value
     */
    data class Loaded<T>(val data: T): Data<T>()

    /**
     * Signifies loading state without any contained data
     */
    class Loading<T>(): Data<T>()
}

/**
 * Checks whether the [Data] reported loaded state but is empty
 * @return true if [Data] reported loaded state but has no items, false otherwise
 */
fun <T, M> Data<T>.loadedEmpty(): Boolean where T: List<M> {
    return this is Data.Loaded && data.isEmpty()
}

/**
 * Checks whether the [Data] reported loaded state and is not empty
 * @return true if [Data] reported loaded state and has items, false otherwise
 */
fun <T, M> Data<T>.loadedData(): Boolean where T: List<M> {
    return this is Data.Loaded && data.isNotEmpty()
}

/**
 * Checks whether the [LazyPagingItems] reported loaded state but is empty
 * @return true if [LazyPagingItems] reported loaded state but has no items, false otherwise
 */
fun <T> LazyPagingItems<T>.loadedEmpty(): Boolean where T: Any {
    return loadState.refresh is LoadState.NotLoading && itemCount == 0
}