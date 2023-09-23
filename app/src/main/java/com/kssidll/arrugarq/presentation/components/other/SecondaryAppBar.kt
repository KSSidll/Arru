package com.kssidll.arrugarq.presentation.components.other

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.presentation.theme.*

@Composable
fun SecondaryAppBar(
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {

        IconButton(
            onClick = onBack
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Go back",
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Preview(
    group = "SecondaryAppBar",
    name = "Secondary App Bar Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    group = "SecondaryAppBar",
    name = "Secondary App Bar Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SecondaryAppBarPreview() {
    ArrugarqTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            SecondaryAppBar(
                onBack = {}
            ) {}
        }
    }
}
