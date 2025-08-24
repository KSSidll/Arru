package com.kssidll.arru.ui.screen.modify.productproducer.addproductproducer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddProductProducerRoute(
    defaultName: String?,
    navigateBack: (productProducerId: Long?) -> Unit,
    viewModel: AddProductProducerViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.screenState.name.value = Field.Loaded(defaultName) }

    ModifyProductProducerScreenImpl(
        onBack = { navigateBack(null) },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                // val result = viewModel.addProducer()
                // if (result.isNotError()) {
                //     navigateBack(result.id)
                // }
            }
        },
    )
}
