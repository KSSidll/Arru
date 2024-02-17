package com.kssidll.arrugarq.ui.screen.modify.shop.addshop

import androidx.compose.runtime.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.shop.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddShopRoute(
    defaultName: String?,
    navigateBack: (shopId: Long?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddShopViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.screenState.name.value = Field.Loaded(defaultName)
    }

    ModifyShopScreenImpl(
        onBack = {
            navigateBack(null)
        },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addShop()
                if (result.isNotError()) {
                    navigateBack(result.id)
                }
            }
        },
    )
}