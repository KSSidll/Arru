package com.kssidll.arru.ui.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * @param year Year for which the main data is fetched
 * @param month Month for which the main data is fetched, in range of 1 - 12
 * @param onMonthIncrement Callback called to request [month] increment, should handle overflow and
 *   increase year
 * @param onMonthDecrement Callback called to request [month] decrement, should handle underflow and
 *   decrease year
 */
@Composable
fun AnalysisDateHeader(
    year: Int,
    month: Int,
    onMonthIncrement: () -> Unit,
    onMonthDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(Calendar.MONTH, month - 1) // calendar has 0 - 11 months

        IconButton(onClick = onMonthDecrement) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(min = 188.dp),
        ) {
            Text(
                text =
                    SimpleDateFormat("LLLL", Locale.getDefault())
                        .format(cal.time)
                        .replaceFirstChar { it.titlecase() },
                style = Typography.headlineLarge,
            )
            Text(text = year.toString(), style = Typography.titleMedium)
        }

        IconButton(onClick = onMonthIncrement) {
            Icon(
                imageVector = Icons.Default.KeyboardDoubleArrowRight,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AnalysisDateHeaderPreview() {
    ArrugarqTheme {
        Surface {
            AnalysisDateHeader(
                year = 2021,
                month = 12,
                onMonthIncrement = {},
                onMonthDecrement = {},
            )
        }
    }
}
