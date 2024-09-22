package com.kssidll.arru.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource

class TransactionBasketWithItemsPagingSource(
    private val transactionRepository: TransactionBasketRepositorySource
): PagingSource<Int, TransactionBasketWithItems>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionBasketWithItems> {
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

    override fun getRefreshKey(state: PagingState<Int, TransactionBasketWithItems>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }
}


