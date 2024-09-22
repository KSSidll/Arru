package com.kssidll.arru.ui.screen.modify.shop.addshop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

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