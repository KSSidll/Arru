package com.kssidll.arrugarq.presentation.screen.addproductcategory

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.presentation.theme.*
import com.kssidll.arrugarq.presentation.widget.other.*


@Composable
fun AddProductCategoryScreen(
    onBack: () -> Unit,
    onCategoryAdd: (AddProductCategoryData) -> Unit,
) {
    Column {
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

        SecondaryAppBar(onBack = onBack) {
            Text(text = stringResource(R.string.item_product_category))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column {
                var name: String by rememberSaveable {
                    mutableStateOf(String())
                }

                var nameError: Boolean by remember {
                    mutableStateOf(false)
                }

                Row(
                    modifier = Modifier.fillMaxHeight(0.6f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {

                    OutlinedTextField(
                        singleLine = true,
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                nameError = name.isEmpty()

                                if (
                                    !nameError
                                ) {
                                    onCategoryAdd(
                                        AddProductCategoryData(name)
                                    )
                                    onBack()
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.outline,
                        ),
                        textStyle = TextStyle.Default.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        ),
                        suffix = {
                            Text(
                                text = stringResource(R.string.item_product_category),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .alpha(0.5F)
                            )
                        },
                        isError = nameError
                    )
                }
                Row(
                    modifier = Modifier.fillMaxHeight(0.4f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {

                    Button(
                        onClick = {
                            nameError = name.isEmpty()

                            if (
                                !nameError
                            ) {
                                onCategoryAdd(
                                    AddProductCategoryData(name)
                                )
                                onBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.add_product_category_description),
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.item_product_category_add),
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    group = "com.kssidll.arrugarq.ui.addproductcategory.AddProductCategoryScreen",
    name = "Add Product Category Screen Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "com.kssidll.arrugarq.ui.addproductcategory.AddProductCategoryScreen",
    name = "Add Product Category Screen Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddProductCategoryScreenPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AddProductCategoryScreen(
                onBack = {},
                onCategoryAdd = {},
            )
        }
    }
}
