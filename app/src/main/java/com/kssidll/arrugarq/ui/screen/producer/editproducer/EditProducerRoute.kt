package com.kssidll.arrugarq.ui.screen.producer.editproducer


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.category.*
import com.kssidll.arrugarq.ui.screen.category.editcategory.*
import com.kssidll.arrugarq.ui.screen.producer.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditProducerRoute(
    producerId: Long,
    onBack: () -> Unit,
    onBackDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditProducerViewModel = hiltViewModel()

    LaunchedEffect(producerId) {
        if (!viewModel.updateState(producerId)) {
            onBack()
        }
    }

    EditProducerScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            viewModel.updateProducer(producerId)
            onBack()
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteProducer(producerId)) {
                    onBackDelete()
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_producer_edit),
    )
}
