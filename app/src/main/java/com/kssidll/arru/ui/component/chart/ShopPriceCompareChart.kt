package com.kssidll.arru.ui.component.chart


import android.graphics.*
import android.text.*
import android.text.style.*
import androidx.compose.animation.core.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.utils.*
import com.kssidll.arru.ui.theme.*
import com.patrykandpatrick.vico.compose.chart.*
import com.patrykandpatrick.vico.compose.chart.line.*
import com.patrykandpatrick.vico.compose.chart.scroll.*
import com.patrykandpatrick.vico.compose.component.*
import com.patrykandpatrick.vico.compose.dimensions.*
import com.patrykandpatrick.vico.compose.legend.*
import com.patrykandpatrick.vico.compose.style.*
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.*
import com.patrykandpatrick.vico.core.extension.*
import com.patrykandpatrick.vico.core.legend.*
import com.patrykandpatrick.vico.core.scroll.*

@Composable
fun ShopPriceCompareChart(
    items: List<ProductPriceByShopByTime>,
    chartEntryModelProducer: ChartEntryModelProducer = remember { ChartEntryModelProducer() },
    chartMinimumEntrySize: Int = 3,
) {
    val data: MutableMap<String, MutableList<ChartEntry>> = remember { mutableMapOf() }

    val chartColorsMap: MutableMap<String, Color> = remember { mutableMapOf() }
    val defaultVariantName = stringResource(id = R.string.item_product_variant_default_value)

    LaunchedEffect(items.size) {
        data.clear()
        chartColorsMap.clear()

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

        items.forEach {
            if (it.shopName == null) return@forEach

            val item = buildString {
                append(it.shopName)

                append(" - ")
                append(it.variantName ?: defaultVariantName)

                if (it.producerName.isNullOrBlank()
                        .not()
                ) {
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

        var offset = 0f
        chartEntryModelProducer.setEntries(
            data.map {
                chartColorsMap[it.key] =
                    Color.hsl(
                        hue = 270f.plus(offset.times(25))
                            .mod(330f)
                            .plus(30f),
                        saturation = 0.4f.plus(offset)
                            .mod(0.8f)
                            .plus(0.2f),
                        lightness = 0.6f.plus(offset)
                            .mod(0.4f)
                            .plus(0.4f),
                    )
                offset += 0.2f

                it.value.toList()
            }
        )
    }

    if (chartEntryModelProducer.getModel()?.entries?.maxOfOrNull { it.size }.orZero > chartMinimumEntrySize) {
        Chart(
            chart = lineChart(
                lines = chartColorsMap.map {
                    lineSpec(
                        lineColor = it.value
                    )
                }
                    .ifEmpty {
                        LocalChartStyle.current.lineChart.lines
                    }
            ),
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
                                .formatToCurrency(),
                            ForegroundColorSpan(model.color),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                        )
                    }
                }
            ),
            legend = verticalLegend(
                items = chartColorsMap.map {
                    LegendItem(
                        icon = shapeComponent(
                            shape = Shapes.pillShape,
                            color = it.value,
                        ),
                        label = textComponent(
                            color = currentChartStyle.axis.axisLabelColor,
                            textSize = 12.sp,
                            typeface = Typeface.MONOSPACE,
                        ),
                        labelText = it.key,
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
fun ShopPriceCompareChartPreview() {
    ArrugarqTheme {
        Surface {
            ShopPriceCompareChart(
                items = ProductPriceByShopByTime.generateList(),
            )
        }
    }
}
