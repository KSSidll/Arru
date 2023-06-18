package com.kssidll.arrugarq.ui.shared

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme

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
                Icons.Rounded.ArrowBack,
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

@Preview(group = "SecondaryAppBar", name = "Secondary App Bar Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(group = "SecondaryAppBar", name = "Secondary App Bar Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SecondaryAppBarPreview() {
    ArrugarqTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            SecondaryAppBar(
                onBack = {}
            ){}
        }
    }
}
