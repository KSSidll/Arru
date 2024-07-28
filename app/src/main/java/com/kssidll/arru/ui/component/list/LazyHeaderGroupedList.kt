package com.kssidll.arru.ui.component.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class LazyHeaderGroupedListHeaderColors(
    val surfaceColor: Color
)

object LazyHeaderGroupedListHeaderDefaults {
    @Composable
    fun colors(
        surfaceColor: Color = MaterialTheme.colorScheme.tertiary
    ): LazyHeaderGroupedListHeaderColors {
        return LazyHeaderGroupedListHeaderColors(
            surfaceColor = surfaceColor,
        )
    }
}


@Composable
fun LazyHeaderGroupedList(
    headerKey: Any? = null,
    headerContentType: Any? = null,
) {

}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.header(
    key: Any?,
    contentType: Any?,
    colors: LazyHeaderGroupedListHeaderColors,
    content: @Composable BoxScope.() -> Unit,
) {
    stickyHeader(
        key = key,
        contentType = contentType,
    ) {
        Surface(
            color = colors.surfaceColor,
        ) {

        }
        Box(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}