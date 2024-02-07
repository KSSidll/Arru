package com.kssidll.arrugarq.data.paging

import androidx.paging.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import kotlin.math.*

class TransactionBasketWithItemsPagingSource(
    private val transactionRepository: TransactionBasketRepositorySource
): PagingSource<Int, TransactionBasketWithItems>() {
    private fun ensureValidKey(key: Int) = max(
        0,
        key
    )

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionBasketWithItems> {
        val start = params.key ?: 0

        return LoadResult.Page(
            data = transactionRepository.transactionBasketsWithItems(
                start,
                params.loadSize
            ),
            prevKey = when (start) {
                0 -> null
                else -> ensureValidKey(start)
            },
            nextKey = start + params.loadSize
        )
    }

    override fun getRefreshKey(state: PagingState<Int, TransactionBasketWithItems>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }
}


