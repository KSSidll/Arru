package com.kssidll.arrugarq.ui.screen.addshop

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
internal fun AddShopScreen(
    onBack: () -> Unit,
    state: AddShopScreenState,
    onShopAdd: () -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(text = stringResource(R.string.item_shop))
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            AddShopScreenContent(
                state = state,
                onShopAdd = onShopAdd,
            )
        }
    }
}

@Composable
private fun AddShopScreenContent(
    state: AddShopScreenState,
    onShopAdd: () -> Unit,
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
                            onShopAdd()
                        }
                    ),
                    label = {
                        Text(
                            text = stringResource(R.string.item_shop),
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
                        onShopAdd()
                    },
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.item_shop_add_description),
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.item_shop_add),
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    group = "Add Shop Screen",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Add Shop Screen",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddShopScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AddShopScreen(
                onBack = {},
                state = AddShopScreenState(),
                onShopAdd = {},
            )
        }
    }
}
