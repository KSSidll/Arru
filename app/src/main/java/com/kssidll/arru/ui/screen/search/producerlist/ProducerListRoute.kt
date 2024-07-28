package com.kssidll.arru.ui.screen.search.producerlist


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.ui.screen.search.shared.SearchList
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ProducerListRoute(
    onProducerClick: (producerId: Long) -> Unit,
    onProducerLongClick: (producerId: Long) -> Unit,
) {
    val viewModel: ProducerListViewModel = hiltViewModel()

    SearchList(
        filter = viewModel.filter,
        onFilterChange = {
            viewModel.filter = it
        },
        items = viewModel.items()
            .collectAsState(initial = Data.Loading()).value,
        onItemClick = {
            onProducerClick(it.id)
        },
        onItemLongClick = {
            onProducerLongClick(it.id)
        },
    )
}
