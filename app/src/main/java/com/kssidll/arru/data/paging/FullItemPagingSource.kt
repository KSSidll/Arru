package com.kssidll.arru.data.paging

import androidx.paging.*
import com.kssidll.arru.data.data.*
import kotlin.math.*

class FullItemPagingSource(
    private val query: suspend (start: Int, loadSize: Int) -> List<FullItem>
): PagingSource<Int, FullItem>() {
    private fun ensureValidKey(key: Int) = max(
        0,
        key
    )

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FullItem> {
        val start = params.key ?: 0

        return LoadResult.Page(
            data = query(
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

    override fun getRefreshKey(state: PagingState<Int, FullItem>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey
        }
    }
}
