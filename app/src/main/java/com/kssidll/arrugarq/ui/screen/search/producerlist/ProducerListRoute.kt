package com.kssidll.arrugarq.ui.screen.search.producerlist


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProducerListRoute(
    onProducerClick: (producerId: Long) -> Unit,
    onProducerLongClick: (producerId: Long) -> Unit,
) {
    val viewModel: ProducerListViewModel = hiltViewModel()

    ListScreen(
        state = viewModel.screenState,
        onItemClick = {
            onProducerClick(it.id)
        },
        onItemLongClick = {
            onProducerLongClick(it.id)
        },
    )
}
