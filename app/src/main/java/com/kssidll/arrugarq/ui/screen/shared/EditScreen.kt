package com.kssidll.arrugarq.ui.screen.shared


import android.content.res.Configuration.*
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
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    onBack: () -> Unit,
    title: String,
    onDelete: (() -> Unit)? = null,
    onSubmit: () -> Unit,
    submitButtonText: String,
    submitButtonDescription: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = {
                    onBack()
                },
                title = {
                    Text(
                        text = title,
                        style = Typography.titleLarge,
                    )
                },
                actions = {
                    if (onDelete != null) {
                        IconButton(
                            onClick = {
                                onDelete()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteForever,
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(27.dp),
                            )
                        }
                    }
                }
            )
        }
    ) {
        Box(Modifier.padding(it)) {
            EditScreenContent(
                onSubmit = onSubmit,
                submitButtonText = submitButtonText,
                submitButtonDescription = submitButtonDescription,
                content = content,
            )
        }
    }
}

private val SubmitButtonHeight: Dp = 70.dp
private val SubmitButtonMaxBottomPadding: Dp = 150.dp
private val SubmitButtonMinBottomPadding: Dp = 6.dp
private val SubmitButtonMaxTopPadding: Dp = 20.dp
private val SubmitButtonMinTopPadding: Dp = 14.dp
private val SubmitButtonHorizontalPadding: Dp = 20.dp

@Composable
private fun EditScreenContent(
    onSubmit: () -> Unit,
    submitButtonText: String,
    submitButtonDescription: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints {
        val boxMaxHeight = maxHeight

        Column {
            val minHeight = boxMaxHeight.minus(SubmitButtonHeight + SubmitButtonMaxBottomPadding)
            val maxHeight =
                boxMaxHeight.minus(SubmitButtonHeight + SubmitButtonMinTopPadding + SubmitButtonMinBottomPadding)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = minHeight,
                        max = maxHeight
                    )
                    .verticalScroll(rememberScrollState())
            ) {

                content()

                Spacer(modifier = Modifier.height(SubmitButtonMaxTopPadding - SubmitButtonMinTopPadding))
            }

            Spacer(modifier = Modifier.height(SubmitButtonMinTopPadding))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    onClick = {
                        onSubmit()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SubmitButtonHeight)
                        .padding(
                            start = SubmitButtonHorizontalPadding,
                            end = SubmitButtonHorizontalPadding,
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = submitButtonDescription,
                        modifier = Modifier.size(26.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = submitButtonText,
                        style = Typography.titleLarge,
                    )
                }
            }
        }
    }
}

@Preview(
    group = "EditScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "EditScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun EditScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            EditScreen(
                onBack = {},
                title = "test",
                onSubmit = {},
                onDelete = {},
                submitButtonDescription = "",
                submitButtonText = "Submit It",
                content = {},
            )
        }
    }
}

@Preview(
    group = "EditScreen",
    name = "Dark No Delete",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "EditScreen",
    name = "Light No Delete",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun EditScreenNoDeletePreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            EditScreen(
                onBack = {},
                title = "test",
                onSubmit = {},
                submitButtonDescription = "",
                submitButtonText = "Submit It",
                content = {},
            )
        }
    }
}
