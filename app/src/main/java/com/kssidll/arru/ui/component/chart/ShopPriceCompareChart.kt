package com.kssidll.arru.ui.component.chart


import android.graphics.Typeface
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kssidll.arru.LocalCurrencyFormatLocale
import com.kssidll.arru.R
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.ProductPriceByShopByTime
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.legend.verticalLegend
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.extension.appendCompat
import com.patrykandpatrick.vico.core.extension.orZero
import com.patrykandpatrick.vico.core.extension.transformToSpannable
import com.patrykandpatrick.vico.core.legend.LegendItem
import com.patrykandpatrick.vico.core.scroll.AutoScrollCondition
import com.patrykandpatrick.vico.core.scroll.InitialScroll

@Composable
fun ShopPriceCompareChart(
    items: List<ProductPriceByShopByTime>,
    chartEntryModelProducer: ChartEntryModelProducer = remember { ChartEntryModelProducer() },
    chartMinimumEntrySize: Int = 2,
) {
    val data: MutableMap<String, MutableList<ChartEntry>> = remember { mutableMapOf() }

    val defaultVariantName = stringResource(id = R.string.item_product_variant_default_value)

    val currencyLocale = LocalCurrencyFormatLocale.current

    LaunchedEffect(items.size) {
        val dateMap: MutableMap<String, Int> = mutableMapOf()
        dateMap.putAll(
            items.mapIndexed { index, it ->
                Pair(
                    it.time,
                    index
                )
            }
                .toMap()
        )

        data.clear()

        items.forEach {
            if (it.shopName == null) return@forEach

            val item = buildString {
                append(it.shopName)

                append(" - ")
                append(it.variantName ?: defaultVariantName)

                if (!it.producerName.isNullOrBlank()) {
                    append(" (")
                    append(it.producerName)
                    append(")")
                }
            }

            data.getOrPut(item) {
                mutableListOf()
            }
                .add(
                    FloatEntry(
                        dateMap[it.time]!!.toFloat(),
                        it.price!!.toFloat()
                    )
                )
        }

        chartEntryModelProducer.setEntries(
            data.map { it.value.toList() }
        )
    }

    AnimatedVisibility(
        visible = chartEntryModelProducer.getModel()?.entries?.maxOfOrNull { it.size }.orZero >= chartMinimumEntrySize,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Chart(
            chart = lineChart(),
            chartModelProducer = chartEntryModelProducer,
            chartScrollSpec = rememberChartScrollSpec(
                isScrollEnabled = true,
                initialScroll = InitialScroll.End,
                autoScrollCondition = AutoScrollCondition.OnModelSizeIncreased,
                autoScrollAnimationSpec = tween(1200),
            ),
            chartScrollState = rememberChartScrollState(),
            marker = rememberMarker(
                labelFormatter = { markedEntries, _ ->
                    markedEntries.transformToSpannable { model ->
                        appendCompat(
                            model.entry.y.div(Item.PRICE_DIVISOR)
                                .formatToCurrency(currencyLocale),
                            ForegroundColorSpan(model.color),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                        )
                    }
                }
            ),
            legend = verticalLegend(
                items = data.toList()
                    .mapIndexed { index, pair ->
                        LegendItem(
                            icon = ShapeComponent(
                                shape = Shapes.pillShape,
                                color = currentChartStyle.lineChart.lines[index.mod(
                                    currentChartStyle.lineChart.lines.size
                                )].lineColor,
                            ),
                            label = textComponent(
                                color = currentChartStyle.axis.axisLabelColor,
                                textSize = 12.sp,
                                typeface = Typeface.MONOSPACE,
                            ),
                            labelText = pair.first,
                        )
                    },
                iconSize = 8.dp,
                iconPadding = 10.dp,
                spacing = 4.dp,
                padding = dimensionsOf(start = 16.dp),
            ),
            isZoomEnabled = false,
        )
    }
}

@PreviewLightDark
@Composable
private fun ShopPriceCompareChartPreview() {
    ArrugarqTheme {
        Surface {
            ShopPriceCompareChart(
                items = ProductPriceByShopByTime.generateList(),
            )
        }
    }
}
