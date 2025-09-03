package com.kssidll.arru.ui.screen.search.productproducerlist

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.search.shared.SearchList
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ProductProducerListRoute(
    onProducerClick: (producerId: Long) -> Unit,
    onProducerLongClick: (producerId: Long) -> Unit,
    viewModel: ProductProducerListViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    SearchList(
        filter = uiState.filter,
        onFilterChange = { viewModel.handleEvent(ProductProducerListSearchEvent.SetFilter(it)) },
        items = uiState.allProductProducers,
        onItemClick = { onProducerClick(it.id) },
        onItemLongClick = { onProducerLongClick(it.id) },
    )
}
