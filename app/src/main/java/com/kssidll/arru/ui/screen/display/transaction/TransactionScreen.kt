package com.kssidll.arru.ui.screen.display.transaction

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionScreen(
    onBack: () -> Unit,
    transaction: TransactionBasketWithItems?,
    onEditAction: () -> Unit,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
) {
    // TODO add adaptive layout handling
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
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (transaction != null) {
                Column(
                    modifier = Modifier.verticalScroll(state = rememberScrollState())
                ) {
                    TransactionBasketCard(
                        transaction = transaction,
                        transactionClickable = false,
                        onItemAddClick = onItemAddClick,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick,
                        onItemCategoryClick = onItemCategoryClick,
                        onItemProducerClick = onItemProducerClick,
                        onItemShopClick = onItemShopClick
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun TransactionScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionScreen(
                onBack = {},
                transaction = TransactionBasketWithItems.generate(),
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
