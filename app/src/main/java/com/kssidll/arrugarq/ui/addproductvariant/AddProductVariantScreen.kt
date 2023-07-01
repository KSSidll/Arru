package com.kssidll.arrugarq.ui.addproductvariant

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.shared.SecondaryAppBar
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme

@Composable
fun AddProductVariantScreen(
    productId: Long,
    onBack: () -> Unit,
    onVariantAdd: (AddProductVariantData) -> Unit,
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
            Text(text = stringResource(R.string.item_full_product_variant))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box (
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column {
                var name: String by rememberSaveable {
                    mutableStateOf(String())
                }

                var nameError: Boolean by remember {
                    mutableStateOf(false)
                }

                Row (
                    modifier = Modifier.fillMaxHeight(0.6f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ){

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
                                    onVariantAdd(
                                        AddProductVariantData(productId, name)
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
                                text = stringResource(R.string.item_product_variant),
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .alpha(0.5F)
                            )
                        },
                        isError = nameError
                    )
                }
                Row (
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
                                onVariantAdd(
                                    AddProductVariantData(productId, name)
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
                                contentDescription = stringResource(R.string.add_product_variant_description),
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.item_product_variant_add),
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(group = "AddProductVariantScreen", name = "Add Product Variant Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(group = "AddProductVariantScreen", name = "Add Product Variant Screen Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun AddProductVariantScreenPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddProductVariantScreen(
                productId = 0,
                onBack = {},
                onVariantAdd = {},
            )
        }
    }
}