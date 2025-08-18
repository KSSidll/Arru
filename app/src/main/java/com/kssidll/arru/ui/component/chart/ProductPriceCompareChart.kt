package com.kssidll.arru.ui.component.chart


import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.collection.FloatFloatPair
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kssidll.arru.LocalCurrencyFormatLocale
import com.kssidll.arru.R
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.domain.data.data.ProductPriceByShopByVariantByProducerByTime
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.collections.immutable.ImmutableList

private val LegendLabelKey = ExtraStore.Key<List<String>>()

@Composable
fun ProductPriceCompareChart(
    items: ImmutableList<ProductPriceByShopByVariantByProducerByTime>,
    chartEntryModelProducer: CartesianChartModelProducer = remember { CartesianChartModelProducer() },
    chartMinimumLineElements: Int = 2,
) {
    var linesOnChart by remember { mutableIntStateOf(0) }


    val defaultVariantName = stringResource(id = R.string.item_product_variant_default_value)

    val currencyLocale = LocalCurrencyFormatLocale.current

    val lineColors = vicoTheme.lineCartesianLayerColors
    val legendItemLabelComponent = rememberTextComponent(
        vicoTheme.textColor,
        textSize = 12.sp,
        typeface = Typeface.MONOSPACE
    )

    LaunchedEffect(items.size) {
        val data: MutableMap<String, MutableList<FloatFloatPair>> = mutableMapOf()

        items.filter { it.value != null }.sortedBy { it.dataOrder }.forEach {
            val item = buildString {
                append(it.productVariantName ?: defaultVariantName)

                if (it.shopName.isNullOrBlank() && it.productProducerName.isNullOrBlank())
                    return@buildString

                if (!it.productProducerName.isNullOrBlank()) {
                    append(" (")
                    append(it.productProducerName)
                    append(")")
                }

                append(" - ")

                if (!it.shopName.isNullOrBlank()) {
                    append(it.shopName)
                }
            }

            val list = data.getOrPut(item) { mutableListOf() }

            list.add(
                FloatFloatPair(
                    it.dataOrder.toFloat(),
                    it.value!!.toFloat().div(ItemEntity.PRICE_DIVISOR)
                )
            )
        }

        val filteredData = data.filter { it.value.isNotEmpty() }

        filteredData.forEach { line ->
            if (line.value.size == 1) {
                val value = line.value.first()
                line.value.add(
                    FloatFloatPair(
                        value.first + 1,
                        value.second
                    )
                )
            }
        }

        linesOnChart = filteredData.size
        if (filteredData.isNotEmpty()) {
            chartEntryModelProducer.runTransaction {
                lineSeries {
                    filteredData.forEach { lineData ->
                        series(
                            x = lineData.value.map { it.first },
                            y = lineData.value.map { it.second }
                        )
                    }
                }

                extras { extraStore ->
                    extraStore[LegendLabelKey] = filteredData.map { it.key }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = linesOnChart >= 1,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        CartesianChartHost(
            modelProducer = chartEntryModelProducer,
            scrollState = rememberVicoScrollState(
                scrollEnabled = true,
                initialScroll = Scroll.Absolute.End,
                autoScrollCondition = AutoScrollCondition.OnModelGrowth,
                autoScrollAnimationSpec = tween(1200)
            ),
            zoomState = rememberVicoZoomState(
                zoomEnabled = false,
                initialZoom = Zoom.fixed(0.03f)
            ),
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        vicoTheme.lineCartesianLayerColors.map { color ->
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(color)),
                                areaFill = LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(color.copy(0.4f), Color.Transparent)
                                        )
                                    )
                                )
                            )
                        }
                    )
                ),
                marker = rememberMarker(
                    valueFormatter = { context, markedEntries ->
                        @Suppress("UNCHECKED_CAST")
                        val lines = markedEntries as List<LineCartesianLayerMarkerTarget>
                        val builder = SpannableStringBuilder()

                        lines.forEach { line ->
                            line.points.forEachIndexed { index, point ->
                                builder.append(
                                    point.entry.y.toFloat().formatToCurrency(currencyLocale),
                                    ForegroundColorSpan(point.color),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )

                                if (index < line.points.size - 1) {
                                    builder.append("  ")
                                }
                            }
                        }

                        builder
                    }
                ),
                legend = rememberVerticalLegend(
                    items = { extraStore ->
                        extraStore.getOrNull(LegendLabelKey)?.forEachIndexed { index, label ->
                            add(
                                LegendItem(
                                    shapeComponent(
                                        fill = fill(lineColors[index % lineColors.size]),
                                        shape = CorneredShape.Pill,
                                    ),
                                    legendItemLabelComponent,
                                    label
                                )
                            )
                        }
                    },
                    iconSize = 8.dp,
                    iconLabelSpacing = 4.dp,
                    rowSpacing = 10.dp,
                    padding = Insets(startDp = 16.dp.value, topDp = 4.dp.value)
                )
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun ProductPriceCompareChartPreview() {
    ArrugarqTheme {
        Surface {
            ProductPriceCompareChart(
                items = ProductPriceByShopByVariantByProducerByTime.generateList(),
            )
        }
    }
}
