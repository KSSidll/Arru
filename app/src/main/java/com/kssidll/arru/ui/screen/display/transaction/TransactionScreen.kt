package com.kssidll.arru.ui.screen.display.transaction

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionScreen(
    onBack: () -> Unit,
    transaction: Data<TransactionBasketWithItems?>,
    onEditAction: () -> Unit,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
) {
    val headerColor = MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {},
                actions = {
                    // 'edit' action
                    IconButton(
                        onClick = {
                            onEditAction()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(id = R.string.edit),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(27.dp)
                        )
                    }
                }
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal),
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
    ) { paddingValues ->
        AnimatedVisibility(
            visible = transaction is Data.Loaded && transaction.data != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            if (transaction is Data.Loaded && transaction.data != null) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                ) {
                    transactionBasketCard(
                        transaction = transaction.data,
                        itemsVisible = true,
                        onItemAddClick = onItemAddClick,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick,
                        onItemCategoryClick = onItemCategoryClick,
                        onItemProducerClick = onItemProducerClick,
                        onItemShopClick = onItemShopClick,
                        headerColor = headerColor,
                        modifier = Modifier.width(600.dp)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewExpanded
@Composable
private fun TransactionScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionScreen(
                onBack = {},
                transaction = Data.Loaded(TransactionBasketWithItems.generate()),
                onEditAction = {},
                onItemAddClick = {},
                onItemClick = {},
                onItemLongClick = {},
                onItemCategoryClick = {},
                onItemProducerClick = {},
                onItemShopClick = {},
            )
        }
    }
}
