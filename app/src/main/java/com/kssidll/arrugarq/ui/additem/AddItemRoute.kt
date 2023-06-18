package com.kssidll.arrugarq.ui.additem

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddItemRoute(
    onBack: () -> Unit
) {
    val addItemViewModel: AddItemViewModel = hiltViewModel()

    AddItemScreen (
        onBack = onBack
    )
}