package com.kssidll.arru.data.paging

import androidx.paging.*
import com.kssidll.arru.data.data.*

class FullItemPagingSource(
    private val query: suspend (start: Int, loadSize: Int) -> List<Item>,
    private val itemsBefore: suspend (id: Long) -> Int,
    private val itemsAfter: suspend (id: Long) -> Int,
): PagingSource<Int, Item>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        val pageIndex = params.key ?: 0

        val page = query(
            pageIndex,
            params.loadSize
        )

        val itemsBefore =
            if (pageIndex == 0) 0
            else page.firstOrNull()?.id?.let { itemsAfter(it) } ?: 0 // reversed since newest first

        val itemsAfter =
            page.lastOrNull()?.id?.let { itemsBefore(it) } ?: 0 // reversed since newest first

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

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }
}
