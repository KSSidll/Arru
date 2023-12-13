package com.kssidll.arrugarq.ui.component.chart


import android.content.res.Configuration.*
import android.graphics.*
import androidx.compose.animation.core.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.theme.*
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
    //    val valueDateMap: MutableMap<Int, String> = remember { mutableMapOf() }

    LaunchedEffect(items.size) {
        data.clear()
        chartColorsMap.clear()
        //        valueDateMap.clear()

        //        valueDateMap.putAll(
        //            items.mapIndexed { index, it ->
        //                Pair(index, it.time)
        //            }.toMap()
        //        )

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

            data.getOrPut(it.shopName) {
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
                        hue = 270f.plus(
                            offset.times(25)
                                .mod(360f)
                        ),
                        saturation = 0.4f.plus(offset)
                            .mod(1f),
                        lightness = 0.6f.plus(offset)
                            .mod(1f),
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
            )
        )
    }
}

@Preview(
    group = "ShopPriceCompareChart",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ShopPriceCompareChart",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ShopPriceCompareChartPreview() {
    ArrugarqTheme {
        Surface {
            ShopPriceCompareChart(
                items = generateRandomProductPriceByShopByTimeList(),
            )
        }
    }
}
