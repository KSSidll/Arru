package com.kssidll.arru.ui.screen.modify.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.FuzzySearchSource
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.StringHelper
import com.kssidll.arru.ui.component.dialog.SearchableListDialog
import com.kssidll.arru.ui.component.field.SearchField
import com.kssidll.arru.ui.component.field.StyledOutlinedTextField
import com.kssidll.arru.ui.screen.modify.ModifyScreen
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerScreenState
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.disabledAlpha
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList

private val ItemHorizontalPadding: Dp = 20.dp

/**
 * [ModifyScreen] implementation for [ProductProducerEntity]
 *
 * @param onBack Called to request a back navigation, isn't triggered by other events like
 *   submission or deletion
 * @param state [ModifyProductProducerScreenState] instance representing the screen state
 * @param shops Shops that can be set for the transaction
 * @param onNewShopSelected Callback called when a new shop is selected. Provides newly selected
 *   shop as parameter
 * @param onSubmit Callback called when the submit action is triggered
 * @param onDelete Callback called when the delete action is triggered, in case of very destructive
 *   actions, should check if delete warning is confirmed, and if not, trigger a delete warning
 *   dialog via showDeleteWarning parameter as none of those are handled internally by the
 *   component, setting to null removes the delete option
 * @param submitButtonText Text displayed in the submit button, defaults to transaction add string
 *   resource
 * @param onShopAddButtonClick Callback called when the shop add button is clicked. Provides a
 *   search value, if any, as parameter
 * @param onTransactionShopLongClick Callback called when the transaction shop is long
 *   clicked/pressed. Provides shop id as parameter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyTransactionScreenImpl(
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
    state: ModifyTransactionScreenState,
    shops: ImmutableList<ShopEntity>,
    onNewShopSelected: (shop: ShopEntity?) -> Unit,
    onSubmit: () -> Unit,
    onShopAddButtonClick: (query: String?) -> Unit,
    onDelete: (() -> Unit)? = null,
    submitButtonText: String = stringResource(id = R.string.transaction_add),
    onTransactionShopLongClick: (shopId: Long) -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    ModifyScreen<FuzzySearchSource>(
        onBack = onBack,
        title = stringResource(id = R.string.transaction),
        onSubmit = onSubmit,
        onDelete = onDelete,
        submitButtonText = submitButtonText,
        showDeleteWarning = state.showDeleteWarning,
        deleteWarningConfirmed = state.deleteWarningConfirmed,
        deleteWarningMessage = stringResource(id = R.string.transaction_delete_warning_text),
    ) {
        if (state.isDatePickerDialogExpanded.value) {
            DatePickerDialog(
                onDismissRequest = { state.isDatePickerDialogExpanded.value = false },
                confirmButton = {
                    Button(
                        onClick = {
                            state.isDatePickerDialogExpanded.value = false
                            state.date.value = Field.Loaded(datePickerState.selectedDateMillis)
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        } else if (state.isShopSearchDialogExpanded.value) {
            SearchableListDialog(
                onDismissRequest = { state.isShopSearchDialogExpanded.value = false },
                items = shops,
                itemText = { it.name },
                onItemClick = {
                    state.isShopSearchDialogExpanded.value = false
                    onNewShopSelected(it)
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    state.isShopSearchDialogExpanded.value = false
                    onTransactionShopLongClick(it.id)
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                onAddButtonClick = onShopAddButtonClick,
                addButtonDescription = stringResource(R.string.item_shop_add_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.no_value),
                calculateScore = { item, query -> item.fuzzyScore(query) },
            )
        }

        if (isExpandedScreen) {
            ExpandedModifyTransactionScreenContent(
                state = state,
                shops = shops,
                onShopAddButtonClick = onShopAddButtonClick,
                onTransactionShopLongClick = onTransactionShopLongClick,
            )
        } else {
            ModifyTransactionScreenContent(
                state = state,
                shops = shops,
                onShopAddButtonClick = onShopAddButtonClick,
                onTransactionShopLongClick = onTransactionShopLongClick,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ModifyTransactionScreenContent(
    state: ModifyTransactionScreenState,
    shops: ImmutableList<ShopEntity>,
    onShopAddButtonClick: (query: String?) -> Unit,
    onTransactionShopLongClick: (shopId: Long) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 650.dp),
    ) {
        val date = state.date.value.data
        SearchField(
            enabled = state.date.value.isEnabled(),
            value =
                if (date != null) SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
                else String(),
            showAddButton = false,
            label = stringResource(R.string.item_date),
            onClick = { state.isDatePickerDialogExpanded.value = true },
            supportingText = {
                if (state.attemptedToSubmit.value) {
                    state.date.value.error?.ErrorText()
                }
            },
            error = if (state.attemptedToSubmit.value) state.date.value.isError() else false,
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding.times(2)),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding.times(2)),
        ) {
            StyledOutlinedTextField(
                singleLine = true,
                enabled = state.totalCost.value.isEnabled(),
                value = state.totalCost.value.data ?: String(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = { newValue ->
                    state.totalCost.apply {
                        if (newValue.isBlank()) {
                            value = Field.Loaded(String())
                        } else if (RegexHelper.isFloat(newValue, 2)) {
                            value = Field.Loaded(newValue)
                        }
                    }
                },
                label = { Text(text = stringResource(R.string.item_price), fontSize = 16.sp) },
                supportingText = {
                    if (state.attemptedToSubmit.value) {
                        state.totalCost.value.error?.ErrorText()
                    }
                },
                isError =
                    if (state.attemptedToSubmit.value) state.totalCost.value.isError() else false,
                modifier = Modifier.weight(1f),
            )

            Column(modifier = Modifier.fillMaxHeight()) {
                IconButton(
                    enabled = state.totalCost.value.isEnabled(),
                    onClick = {
                        if (state.totalCost.value.data.isNullOrBlank()) {
                            state.totalCost.value = Field.Loaded("%.2f".format(0f))
                        } else {
                            val value =
                                state.totalCost.value.data!!.let { StringHelper.toDoubleOrNull(it) }

                            if (value != null) {
                                state.totalCost.value =
                                    Field.Loaded("%.2f".format(value.plus(0.5f)))
                            }
                        }
                    },
                    colors =
                        IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor =
                                MaterialTheme.colorScheme.primary.copy(disabledAlpha),
                        ),
                    modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowUp,
                        contentDescription =
                            stringResource(id = R.string.item_price_increment_by_half),
                    )
                }

                IconButton(
                    enabled = state.totalCost.value.isEnabled(),
                    onClick = {
                        if (state.totalCost.value.data.isNullOrBlank()) {
                            state.totalCost.value = Field.Loaded("%.2f".format(0f))
                        } else {
                            val value =
                                state.totalCost.value.data!!.let { StringHelper.toDoubleOrNull(it) }

                            if (value != null) {
                                state.totalCost.value =
                                    Field.Loaded(
                                        "%.2f"
                                            .format(
                                                if (value > 0.5f) value.minus(0.5f)
                                                else {
                                                    0f
                                                }
                                            )
                                    )
                            }
                        }
                    },
                    colors =
                        IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor =
                                MaterialTheme.colorScheme.primary.copy(disabledAlpha),
                        ),
                    modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription =
                            stringResource(id = R.string.item_price_decrement_by_half),
                    )
                }
            }
        }

        HorizontalDivider()

        Spacer(modifier = Modifier.height(8.dp))

        StyledOutlinedTextField(
            optional = state.note.value.data.isNullOrBlank(),
            enabled = state.note.value.isEnabled(),
            value = state.note.value.data.orEmpty(),
            onValueChange = { newValue ->
                state.note.apply { value = Field.Loaded(newValue.trimStart()) }
            },
            label = { Text(text = stringResource(R.string.transaction_note), fontSize = 16.sp) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding.times(2)),
        )

        Spacer(modifier = Modifier.height(8.dp))

        SearchField(
            enabled = state.selectedShop.value.isEnabled(),
            optional = true,
            value = state.selectedShop.value.data?.name ?: String(),
            onClick = {
                if (shops.isNotEmpty()) {
                    state.isShopSearchDialogExpanded.value = true
                } else {
                    onShopAddButtonClick(null)
                }
            },
            onLongClick = {
                state.selectedShop.value.data?.let { onTransactionShopLongClick(it.id) }
            },
            label = stringResource(R.string.item_shop),
            onAddButtonClick = { onShopAddButtonClick(null) },
            addButtonDescription = stringResource(R.string.item_shop_add_description),
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
        )
    }
}

@Composable
private fun ExpandedModifyTransactionScreenContent(
    state: ModifyTransactionScreenState,
    shops: ImmutableList<ShopEntity>,
    onShopAddButtonClick: (query: String?) -> Unit,
    onTransactionShopLongClick: (shopId: Long) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 650.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val date = state.date.value.data
            SearchField(
                enabled = state.date.value.isEnabled(),
                value =
                    if (date != null)
                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
                    else String(),
                showAddButton = false,
                label = stringResource(R.string.item_date),
                onClick = { state.isDatePickerDialogExpanded.value = true },
                supportingText = {
                    if (state.attemptedToSubmit.value) {
                        state.date.value.error?.ErrorText()
                    }
                },
                error = if (state.attemptedToSubmit.value) state.date.value.isError() else false,
                modifier =
                    Modifier.weight(1f)
                        .padding(
                            start = ItemHorizontalPadding.times(2),
                            end = ItemHorizontalPadding.div(2),
                        ),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).padding(start = ItemHorizontalPadding.div(2)),
            ) {
                StyledOutlinedTextField(
                    singleLine = true,
                    enabled = state.totalCost.value.isEnabled(),
                    value = state.totalCost.value.data ?: String(),
                    keyboardOptions =
                        KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { newValue ->
                        state.totalCost.apply {
                            if (newValue.isBlank()) {
                                value = Field.Loaded(String())
                            } else if (RegexHelper.isFloat(newValue, 2)) {
                                value = Field.Loaded(newValue)
                            }
                        }
                    },
                    label = { Text(text = stringResource(R.string.item_price), fontSize = 16.sp) },
                    supportingText = {
                        if (state.attemptedToSubmit.value) {
                            state.totalCost.value.error?.ErrorText()
                        }
                    },
                    isError =
                        if (state.attemptedToSubmit.value) state.totalCost.value.isError()
                        else false,
                    modifier = Modifier.weight(1f),
                )

                Column(modifier = Modifier.fillMaxHeight()) {
                    IconButton(
                        enabled = state.totalCost.value.isEnabled(),
                        onClick = {
                            if (state.totalCost.value.data.isNullOrBlank()) {
                                state.totalCost.value = Field.Loaded("%.2f".format(0f))
                            } else {
                                val value =
                                    state.totalCost.value.data!!.let {
                                        StringHelper.toDoubleOrNull(it)
                                    }

                                if (value != null) {
                                    state.totalCost.value =
                                        Field.Loaded("%.2f".format(value.plus(0.5f)))
                                }
                            }
                        },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor =
                                    MaterialTheme.colorScheme.primary.copy(disabledAlpha),
                            ),
                        modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowUp,
                            contentDescription =
                                stringResource(id = R.string.item_price_increment_by_half),
                        )
                    }

                    IconButton(
                        enabled = state.totalCost.value.isEnabled(),
                        onClick = {
                            if (state.totalCost.value.data.isNullOrBlank()) {
                                state.totalCost.value = Field.Loaded("%.2f".format(0f))
                            } else {
                                val value =
                                    state.totalCost.value.data!!.let {
                                        StringHelper.toDoubleOrNull(it)
                                    }

                                if (value != null) {
                                    state.totalCost.value =
                                        Field.Loaded(
                                            "%.2f"
                                                .format(
                                                    if (value > 0.5f) value.minus(0.5f)
                                                    else {
                                                        0f
                                                    }
                                                )
                                        )
                                }
                            }
                        },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContentColor =
                                    MaterialTheme.colorScheme.primary.copy(disabledAlpha),
                            ),
                        modifier = Modifier.minimumInteractiveComponentSize(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription =
                                stringResource(id = R.string.item_price_decrement_by_half),
                        )
                    }
                }
            }
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        SearchField(
            enabled = state.selectedShop.value.isEnabled(),
            optional = true,
            value = state.selectedShop.value.data?.name ?: String(),
            onClick = {
                if (shops.isNotEmpty()) {
                    state.isShopSearchDialogExpanded.value = true
                } else {
                    onShopAddButtonClick(null)
                }
            },
            onLongClick = {
                state.selectedShop.value.data?.let { onTransactionShopLongClick(it.id) }
            },
            label = stringResource(R.string.item_shop),
            onAddButtonClick = { onShopAddButtonClick(null) },
            addButtonDescription = stringResource(R.string.item_shop_add_description),
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
        )
    }
}

@PreviewLightDark
@Composable
private fun ModifyTransactionScreenImplPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyTransactionScreenImpl(
                isExpandedScreen = false,
                onBack = {},
                state = ModifyTransactionScreenState(),
                shops = emptyImmutableList(),
                onNewShopSelected = {},
                onSubmit = {},
                onShopAddButtonClick = {},
                onTransactionShopLongClick = {},
            )
        }
    }
}

@PreviewExpanded
@Composable
private fun ExpandedModifyTransactionScreenImplPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyTransactionScreenImpl(
                isExpandedScreen = true,
                onBack = {},
                state = ModifyTransactionScreenState(),
                shops = emptyImmutableList(),
                onNewShopSelected = {},
                onSubmit = {},
                onShopAddButtonClick = {},
                onTransactionShopLongClick = {},
            )
        }
    }
}
