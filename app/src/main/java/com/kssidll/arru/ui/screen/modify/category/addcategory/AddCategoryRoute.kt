package com.kssidll.arru.ui.screen.modify.category.addcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.category.ModifyCategoryScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddCategoryRoute(
    defaultName: String?,
    navigateBack: (categoryId: Long?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddCategoryViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.screenState.name.value = Field.Loaded(defaultName)
    }

    ModifyCategoryScreenImpl(
        onBack = {
            navigateBack(null)
        },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addCategory()
                if (result.isNotError()) {
                    navigateBack(result.id)
                }
            }
        },
    )
}

