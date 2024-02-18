package com.kssidll.arru.ui.screen.home.analysis.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.ui.theme.*
import java.text.*
import java.util.*

/**
 * @param year Year for which the main data is fetched
 * @param month Month for which the main data is fetched, in range of 1 - 12
 * @param onMonthIncrement Callback called to request [month] increment, should handle overflow and increase year
 * @param onMonthDecrement Callback called to request [month] decrement, should handle underflow and decrease year
 */
@Composable
fun DateHeader(
    year: Int,
    month: Int,
    onMonthIncrement: () -> Unit,
    onMonthDecrement: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(
            Calendar.MONTH,
            month - 1
        ) // calendar has 0 - 11 months

        IconButton(onClick = onMonthDecrement) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(min = 188.dp)
        ) {
            Text(
                text = SimpleDateFormat(
                    "LLLL",
                    Locale.getDefault()
                ).format(cal.time)
                    .replaceFirstChar { it.titlecase() },
                style = Typography.headlineLarge,
            )
            Text(
                text = year.toString(),
                style = Typography.titleMedium,
            )
        }

        IconButton(onClick = onMonthIncrement) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun DateHeaderPreview() {
    ArrugarqTheme {
        Surface {
            DateHeader(
                year = 2021,
                month = 12,
                onMonthIncrement = {},
                onMonthDecrement = {},
            )
        }
    }
}
