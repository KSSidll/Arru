package com.kssidll.arrugarq.ui.screen.home.search.producerlist


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.home.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProducerListRoute(
    onProducerSelect: (producerId: Long) -> Unit,
    onProducerEdit: (producerId: Long) -> Unit,
) {
    val viewModel: ProducerListViewModel = hiltViewModel()

    ListScreen(
        state = viewModel.screenState,
        onItemSelect = {
            onProducerSelect(it.id)
        },
        onItemEdit = {
            onProducerEdit(it.id)
        },
    )
}
