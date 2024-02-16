package com.kssidll.arrugarq.ui.screen.modify.category.addcategory

import androidx.compose.runtime.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.category.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddCategoryRoute(
    defaultName: String?,
    navigateBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddCategoryViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.screenState.name.value = Field.Loaded(defaultName)
    }

    ModifyCategoryScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                if (viewModel.addCategory()
                        .isNotError()
                ) {
                    navigateBack()
                }
            }
        },
    )
}

