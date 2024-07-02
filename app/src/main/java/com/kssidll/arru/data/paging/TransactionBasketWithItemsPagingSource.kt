package com.kssidll.arru.data.paging

import androidx.paging.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*

class TransactionBasketWithItemsPagingSource(
    private val transactionRepository: TransactionBasketRepositorySource
): PagingSource<Int, Transaction>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        val pageIndex = params.key ?: 0

        val page = transactionRepository.transactionBasketsWithItems(
            pageIndex,
            params.loadSize
        )

        val itemsBefore =
            if (pageIndex == 0) 0
            else page.firstOrNull()?.id?.let { transactionRepository.countAfter(it) }
                ?: 0 // reversed since newest first

        val itemsAfter = page.lastOrNull()?.id?.let { transactionRepository.countBefore(it) }
            ?: 0 // reversed since newest first

        val prevKey = if (pageIndex == 0) null else pageIndex - params.loadSize
        val nextKey = if (itemsAfter == 0) null else pageIndex + params.loadSize

        return LoadResult.Page(
            data = page,
            prevKey = prevKey,
            nextKey = nextKey,
            itemsBefore = itemsBefore,
            itemsAfter = itemsAfter
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }
}


