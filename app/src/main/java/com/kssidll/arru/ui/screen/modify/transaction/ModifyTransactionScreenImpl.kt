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
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.component.dialog.SearchableListDialog
import com.kssidll.arru.ui.component.field.SearchField
import com.kssidll.arru.ui.component.field.StyledOutlinedTextField
import com.kssidll.arru.ui.screen.modify.ModifyScreen
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.disabledAlpha
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val ItemHorizontalPadding: Dp = 20.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyTransactionScreenImpl(
    isExpandedScreen: Boolean,
    uiState: ModifyTransactionUiState,
    onEvent: (event: ModifyTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
    submitButtonText: String = stringResource(id = R.string.item_add),
) {
    val datePickerState = rememberDatePickerState()

    ModifyScreen(
        onBack = { onEvent(ModifyTransactionEvent.NavigateBack) },
        title = stringResource(id = R.string.transaction),
        onSubmit = { onEvent(ModifyTransactionEvent.Submit) },
        submitButtonText = submitButtonText,
        onDelete = { onEvent(ModifyTransactionEvent.DeleteTransaction) },
        isDeleteVisible = uiState.isDeleteEnabled,
        isDeleteWarningMessageVisible = uiState.isDangerousDeleteDialogVisible,
        onDeleteWarningMessageVisibleChange = {
            onEvent(ModifyTransactionEvent.SetDangerousDeleteDialogVisibility(it))
        },
        deleteWarningMessage = stringResource(id = R.string.transaction_delete_warning_text),
        isDeleteWarningConfirmed = uiState.isDangerousDeleteDialogConfirmed,
        onDeleteWarningConfirmedChange = {
            onEvent(ModifyTransactionEvent.SetDangerousDeleteDialogConfirmation(it))
        },
        onMerge = {},
        isMergeVisible = false,
        isMergeSearchDialogVisible = false,
        onMergeSearchDialogVisibleChange = {},
        mergeSearchDialogCandidateTextTransformation = { String() },
        isMergeConfirmVisible = false,
        onMergeConfirmVisibleChange = {},
        mergeConfirmMessage = String(),
        mergeCandidates = emptyImmutableList(),
        onChosenMergeCandidateChange = {},
        modifier = modifier,
    ) {
        if (uiState.isDatePickerDialogVisible) {
            DatePickerDialog(
                onDismissRequest = {
                    onEvent(ModifyTransactionEvent.SetDatePickerDialogVisibility(false))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onEvent(ModifyTransactionEvent.SetDatePickerDialogVisibility(false))
                            onEvent(
                                ModifyTransactionEvent.SetDate(datePickerState.selectedDateMillis)
                            )
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
        } else if (uiState.isShopSearchDialogVisible) {
            SearchableListDialog(
                onDismissRequest = {
                    onEvent(ModifyTransactionEvent.SetShopSearchDialogVisibility(false))
                },
                items = uiState.allShops,
                itemText = { it.name },
                onItemClick = {
                    onEvent(ModifyTransactionEvent.SetShopSearchDialogVisibility(false))
                    onEvent(ModifyTransactionEvent.SelectShop(it?.id))
                },
                onItemClickLabel = stringResource(id = R.string.select),
                onItemLongClick = {
                    onEvent(ModifyTransactionEvent.SetShopSearchDialogVisibility(false))
                    onEvent(ModifyTransactionEvent.NavigateEditShop(it.id))
                },
                onItemLongClickLabel = stringResource(id = R.string.edit),
                onAddButtonClick = { onEvent(ModifyTransactionEvent.NavigateAddShop(it)) },
                addButtonDescription = stringResource(R.string.item_shop_add_description),
                showDefaultValueItem = true,
                defaultItemText = stringResource(R.string.no_value),
                calculateScore = { item, query -> item.fuzzyScore(query) },
            )
        }

        if (isExpandedScreen) {
            ExpandedModifyTransactionScreenContent(uiState = uiState, onEvent = onEvent)
        } else {
            ModifyTransactionScreenContent(uiState = uiState, onEvent = onEvent)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ModifyTransactionScreenContent(
    uiState: ModifyTransactionUiState,
    onEvent: (event: ModifyTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.widthIn(max = 650.dp),
    ) {
        val date = uiState.date.data
        SearchField(
            enabled = uiState.date.isLoading(),
            value =
                if (date != null)
                    SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        .format(date - TimeZone.getDefault().getOffset(date))
                else String(),
            showAddButton = false,
            label = stringResource(R.string.item_date),
            onClick = { onEvent(ModifyTransactionEvent.SetDatePickerDialogVisibility(true)) },
            supportingText = { uiState.date.error?.ErrorText() },
            error = uiState.date.isError(),
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding.times(2)),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding.times(2)),
        ) {
            StyledOutlinedTextField(
                singleLine = true,
                enabled = uiState.totalCost.isLoading(),
                value = uiState.totalCost.data,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = { newValue ->
                    onEvent(ModifyTransactionEvent.SetTotalCost(newValue))
                },
                label = { Text(text = stringResource(R.string.item_price), fontSize = 16.sp) },
                supportingText = { uiState.totalCost.error?.ErrorText() },
                isError = uiState.totalCost.isError(),
                modifier = Modifier.weight(1f),
            )

            Column(modifier = Modifier.fillMaxHeight()) {
                IconButton(
                    enabled = uiState.totalCost.isLoading(),
                    onClick = { onEvent(ModifyTransactionEvent.IncrementTotalCost) },
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
                    enabled = uiState.totalCost.isLoading(),
                    onClick = { onEvent(ModifyTransactionEvent.DecrementTotalCost) },
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
            optional = uiState.note.data.isNullOrBlank(),
            enabled = uiState.note.isLoading(),
            value = uiState.note.data.orEmpty(),
            onValueChange = { newValue -> onEvent(ModifyTransactionEvent.SetNote(newValue)) },
            label = { Text(text = stringResource(R.string.transaction_note), fontSize = 16.sp) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding.times(2)),
        )

        Spacer(modifier = Modifier.height(8.dp))

        SearchField(
            enabled = uiState.selectedShop.isLoading(),
            optional = true,
            value = uiState.selectedShop.data?.name ?: String(),
            onClick = {
                if (uiState.allShops.isNotEmpty()) {
                    onEvent(ModifyTransactionEvent.SetShopSearchDialogVisibility(true))
                } else {
                    onEvent(ModifyTransactionEvent.NavigateAddShop(null))
                }
            },
            onLongClick = {
                uiState.selectedShop.data?.let {
                    onEvent(ModifyTransactionEvent.NavigateEditShop(it.id))
                }
            },
            label = stringResource(R.string.item_shop),
            onAddButtonClick = { onEvent(ModifyTransactionEvent.NavigateAddShop(null)) },
            addButtonDescription = stringResource(R.string.item_shop_add_description),
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
        )
    }
}

@Composable
private fun ExpandedModifyTransactionScreenContent(
    uiState: ModifyTransactionUiState,
    onEvent: (event: ModifyTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.widthIn(max = 650.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val date = uiState.date.data
            SearchField(
                enabled = uiState.date.isLoading(),
                value =
                    if (date != null)
                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                            .format(date - TimeZone.getDefault().getOffset(date))
                    else String(),
                showAddButton = false,
                label = stringResource(R.string.item_date),
                onClick = { onEvent(ModifyTransactionEvent.SetDatePickerDialogVisibility(true)) },
                supportingText = { uiState.date.error?.ErrorText() },
                error = uiState.date.isError(),
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
                    enabled = uiState.totalCost.isLoading(),
                    value = uiState.totalCost.data,
                    keyboardOptions =
                        KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { newValue ->
                        onEvent(ModifyTransactionEvent.SetTotalCost(newValue))
                    },
                    label = { Text(text = stringResource(R.string.item_price), fontSize = 16.sp) },
                    supportingText = { uiState.totalCost.error?.ErrorText() },
                    isError = uiState.totalCost.isError(),
                    modifier = Modifier.weight(1f),
                )

                Column(modifier = Modifier.fillMaxHeight()) {
                    IconButton(
                        enabled = uiState.totalCost.isLoading(),
                        onClick = { onEvent(ModifyTransactionEvent.IncrementTotalCost) },
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
                        enabled = uiState.totalCost.isLoading(),
                        onClick = { onEvent(ModifyTransactionEvent.DecrementTotalCost) },
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
            enabled = uiState.selectedShop.isLoading(),
            optional = true,
            value = uiState.selectedShop.data?.name ?: String(),
            onClick = {
                if (uiState.allShops.isNotEmpty()) {
                    onEvent(ModifyTransactionEvent.SetShopSearchDialogVisibility(true))
                } else {
                    onEvent(ModifyTransactionEvent.NavigateAddShop(null))
                }
            },
            onLongClick = {
                uiState.selectedShop.data?.let {
                    onEvent(ModifyTransactionEvent.NavigateEditShop(it.id))
                }
            },
            label = stringResource(R.string.item_shop),
            onAddButtonClick = { onEvent(ModifyTransactionEvent.NavigateAddShop(null)) },
            addButtonDescription = stringResource(R.string.item_shop_add_description),
            modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
        )
    }
}

@PreviewLightDark
@Composable
private fun ModifyTransactionScreenImplPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyTransactionScreenImpl(
                isExpandedScreen = false,
                uiState = ModifyTransactionUiState(isDeleteEnabled = true),
                onEvent = {},
            )
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedModifyTransactionScreenImplPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyTransactionScreenImpl(
                isExpandedScreen = true,
                uiState = ModifyTransactionUiState(isDeleteEnabled = true),
                onEvent = {},
            )
        }
    }
}
