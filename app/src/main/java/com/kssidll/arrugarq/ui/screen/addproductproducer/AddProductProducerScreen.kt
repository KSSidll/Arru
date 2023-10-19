package com.kssidll.arrugarq.ui.screen.addproductproducer

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.component.field.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddProductProducerScreen(
    onBack: () -> Unit,
    state: AddProductProducerScreenState,
    onProducerAdd: () -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(text = stringResource(R.string.item_product_producer_full))
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            AddProductProducerScreenContent(
                state = state,
                onProducerAdd = onProducerAdd,
            )
        }
    }
}

@Composable
private fun AddProductProducerScreenContent(
    state: AddProductProducerScreenState,
    onProducerAdd: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                focusRequester.requestFocus()
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxHeight(0.6f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {

                StyledOutlinedTextField(
                    singleLine = true,
                    value = state.name.value,
                    onValueChange = {
                        state.name.value = it
                        state.validateName()
                    },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onProducerAdd()
                        }
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.item_product_producer),
                        )
                    },
                    isError = if (state.attemptedToSubmit.value) state.nameError.value else false,
                )
            }
            Row(
                modifier = Modifier.fillMaxHeight(0.4f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    onClick = {
                        onProducerAdd()
                    },
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.item_product_producer_add_description),
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.item_product_producer_add),
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    group = "Add Product Producer Screen",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Add Product Producer Screen",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddProductProducerScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AddProductProducerScreen(
                onBack = {},
                state = AddProductProducerScreenState(),
                onProducerAdd = {},
            )
        }
    }
}
