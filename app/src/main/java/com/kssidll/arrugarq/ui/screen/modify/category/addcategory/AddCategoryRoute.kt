package com.kssidll.arrugarq.ui.screen.modify.category.addcategory

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.modify.category.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddCategoryRoute(
    navigateBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddCategoryViewModel = hiltViewModel()

    ModifyCategoryScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addCategory()
                if (result != null) navigateBack()
            }
        },
    )
}

