package com.kssidll.arru.ui.screen.modify.productvariant.addproductvariant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun AddProductVariantRoute(
    productId: Long,
    defaultName: String?,
    navigateBack: (productVariantId: Long?) -> Unit,
    viewModel: AddProductVariantViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }
    // TODO check productId

    LaunchedEffect(Unit) { viewModel.screenState.name.value = Field.Loaded(defaultName) }

    ModifyProductVariantScreenImpl(
        onBack = {
            if (!navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack(null)
            }
        },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                // val result = viewModel.addVariant(productId)
                // if (result.isNotError() && !navigateBackLock.isLocked) {
                // navigateBackLock.tryLock()
                //     navigateBack(result.id)
                // }
            }
        },
    )
}
