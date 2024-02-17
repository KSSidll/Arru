/*
 * Copyright 2023 by Patryk Goworowski and Patrick Michalik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* https://github.com/patrykandpatrick/vico/blob/master/sample/src/main/java/com/patrykandpatrick/vico/sample/showcase/Marker.kt */

package com.kssidll.arru.ui.component.chart

import android.graphics.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.patrykandpatrick.vico.compose.component.*
import com.patrykandpatrick.vico.compose.dimensions.*
import com.patrykandpatrick.vico.core.chart.dimensions.*
import com.patrykandpatrick.vico.core.chart.insets.Insets
import com.patrykandpatrick.vico.core.component.marker.*
import com.patrykandpatrick.vico.core.component.shape.*
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.*
import com.patrykandpatrick.vico.core.context.*
import com.patrykandpatrick.vico.core.extension.*
import com.patrykandpatrick.vico.core.marker.*

private const val LABEL_BACKGROUND_SHADOW_RADIUS = 4f
private const val LABEL_BACKGROUND_SHADOW_DY = 2f
private const val LABEL_LINE_COUNT = 1
private const val GUIDELINE_ALPHA = .2f
private const val INDICATOR_SIZE_DP = 36f
private const val INDICATOR_OUTER_COMPONENT_ALPHA = 32
private const val INDICATOR_CENTER_COMPONENT_SHADOW_RADIUS = 12f
private const val GUIDELINE_DASH_LENGTH_DP = 8f
private const val GUIDELINE_GAP_LENGTH_DP = 4f
private const val SHADOW_RADIUS_MULTIPLIER = 1.3f

private val labelBackgroundShape = MarkerCorneredShape(Corner.FullyRounded)
private val labelHorizontalPaddingValue = 8.dp
private val labelVerticalPaddingValue = 4.dp
private val labelPadding = dimensionsOf(
    labelHorizontalPaddingValue,
    labelVerticalPaddingValue
)
private val indicatorInnerAndCenterComponentPaddingValue = 5.dp
private val indicatorCenterAndOuterComponentPaddingValue = 10.dp
private val guidelineThickness = 2.dp
private val guidelineShape = DashedShape(
    Shapes.pillShape,
    GUIDELINE_DASH_LENGTH_DP,
    GUIDELINE_GAP_LENGTH_DP
)

@Composable
internal fun rememberMarker(labelFormatter: MarkerLabelFormatter = DefaultMarkerLabelFormatter()): Marker {
    val labelBackgroundColor = MaterialTheme.colorScheme.surface
    val labelBackground =
        remember(labelBackgroundColor) {
            ShapeComponent(
                labelBackgroundShape,
                labelBackgroundColor.toArgb()
            ).setShadow(
                radius = LABEL_BACKGROUND_SHADOW_RADIUS,
                dy = LABEL_BACKGROUND_SHADOW_DY,
                applyElevationOverlay = true,
            )
        }
    val label =
        textComponent(
            background = labelBackground,
            lineCount = LABEL_LINE_COUNT,
            padding = labelPadding,
            typeface = Typeface.MONOSPACE,
        )
    val indicatorInnerComponent = shapeComponent(
        Shapes.pillShape,
        MaterialTheme.colorScheme.surface
    )
    val indicatorCenterComponent = shapeComponent(
        Shapes.pillShape,
        Color.White
    )
    val indicatorOuterComponent = shapeComponent(
        Shapes.pillShape,
        Color.White
    )
    val indicator =
        overlayingComponent(
            outer = indicatorOuterComponent,
            inner =
            overlayingComponent(
                outer = indicatorCenterComponent,
                inner = indicatorInnerComponent,
                innerPaddingAll = indicatorInnerAndCenterComponentPaddingValue,
            ),
            innerPaddingAll = indicatorCenterAndOuterComponentPaddingValue,
        )
    val guideline =
        lineComponent(
            MaterialTheme.colorScheme.onSurface.copy(GUIDELINE_ALPHA),
            guidelineThickness,
            guidelineShape,
        )
    return remember(
        label,
        indicator,
        guideline
    ) {
        object: MarkerComponent(
            label,
            indicator,
            guideline
        ) {
            init {
                indicatorSizeDp = INDICATOR_SIZE_DP
                onApplyEntryColor = { entryColor ->
                    indicatorOuterComponent.color =
                        entryColor.copyColor(INDICATOR_OUTER_COMPONENT_ALPHA)
                    with(indicatorCenterComponent) {
                        color = entryColor
                        setShadow(
                            radius = INDICATOR_CENTER_COMPONENT_SHADOW_RADIUS,
                            color = entryColor
                        )
                    }
                }

                this.labelFormatter = labelFormatter
            }

            override fun getInsets(
                context: MeasureContext,
                outInsets: Insets,
                horizontalDimensions: HorizontalDimensions,
            ) = with(context) {
                outInsets.top = label.getHeight(context) + labelBackgroundShape.tickSizeDp.pixels +
                        LABEL_BACKGROUND_SHADOW_RADIUS.pixels * SHADOW_RADIUS_MULTIPLIER -
                        LABEL_BACKGROUND_SHADOW_DY.pixels
            }
        }
    }
}