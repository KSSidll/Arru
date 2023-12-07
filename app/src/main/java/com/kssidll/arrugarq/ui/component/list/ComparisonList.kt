package com.kssidll.arrugarq.ui.component.list


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.theme.*
import kotlin.math.*


private val ITEM_HEIGHT: Dp = 90.dp
private val HEADER_HEIGHT: Dp = 24.dp
private val DIFF_PERCENT_STRING_BOTTOM_PADDING: Dp = 16.dp

/**
 * @param rightSideItems List of items represented on the right side, these are compared to [leftSideItems] and take sorting priority
 * @param rightSideHeader String displayed as [rightSideItems] header
 * @param leftSideItems List of items represented on the left side
 * @param leftSideHeader String displayed as [leftSideItems] header
 */
@Composable
fun <T> ComparisonList(
    rightSideItems: List<T>,
    rightSideHeader: String,
    leftSideItems: List<T>,
    leftSideHeader: String,
) where T: RankSource {
    Row(modifier = Modifier.padding(horizontal = 12.dp)) {
        val leftGroupped = leftSideItems.groupBy { it.displayName() }
        val rightSorted = rightSideItems.sortedByDescending { it.sortValue() }
        val rightGroupped = rightSideItems.groupBy { it.displayName() }
        val names = rightSorted.plus(
            leftSideItems.filterNot { it.displayName() in rightGroupped }
                .sortedBy { it.sortValue() }
        )
            .map { it.displayName() }

        Column(modifier = Modifier.width(IntrinsicSize.Min)) {
            Spacer(modifier = Modifier.height(HEADER_HEIGHT))

            names.forEach {
                Box(modifier = Modifier.height(ITEM_HEIGHT)) {
                    Text(
                        text = it,
                        style = Typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
                HorizontalDivider()
            }
        }

        Column(modifier = Modifier.weight(1F)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HEADER_HEIGHT)
            ) {
                Text(
                    text = leftSideHeader,
                    style = Typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            names.forEach {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                ) {
                    val item = leftGroupped[it]
                    if (item != null) {
                        Text(
                            text = item[0].displayValue(),
                            style = Typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                HorizontalDivider()
            }
        }

        Column(modifier = Modifier.weight(1F)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HEADER_HEIGHT)
            ) {
                Text(
                    text = rightSideHeader,
                    style = Typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            names.forEach {
                Box(
                    modifier = Modifier
                        .height(ITEM_HEIGHT)
                        .fillMaxWidth()
                ) {
                    val item = rightGroupped[it]
                    val otherItem = leftGroupped[it]
                    if (item != null) {
                        Text(
                            text = item[0].displayValue(),
                            style = Typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        if (otherItem != null) {
                            val itemValue = item[0].value()
                                .roundToLong()
                            val otherItemValue = otherItem[0].value()
                                .roundToLong()

                            val diffStr: String = if (itemValue > otherItemValue) {
                                "+${
                                    (itemValue.toDouble()
                                        .div(otherItemValue)).minus(1)
                                        .times(100)
                                        .toLong()
                                } %"
                            } else {
                                "-${
                                    (otherItemValue.toDouble()
                                        .div(itemValue)).minus(1)
                                        .times(100)
                                        .toLong()
                                } %"
                            }

                            if (itemValue != otherItemValue) {
                                Text(
                                    text = diffStr,
                                    style = Typography.bodySmall,
                                    color = if (diffStr[0] == '-') {
                                        MaterialTheme.colorScheme.tertiary.copy(optionalAlpha)
                                    } else {
                                        MaterialTheme.colorScheme.error.copy(optionalAlpha)
                                    },
                                    modifier = Modifier
                                        .padding(bottom = DIFF_PERCENT_STRING_BOTTOM_PADDING)
                                        .align(Alignment.BottomCenter)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Preview(
    group = "ComparisonList",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ComparisonList",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
private fun ComparisonListPreview() {
    ArrugarqTheme {
        Surface {
            ComparisonList(
                rightSideItems = emptyList(),
                rightSideHeader = "test",
                leftSideItems = emptyList(),
                leftSideHeader = "test",
            )
        }
    }
}
