package com.kssidll.arru.ui.component.other


import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kssidll.arru.ui.theme.ArrugarqTheme

/**
 * A progress bar component
 * @param progressValue Percentage, from 0.0 to 1.0, of the progress bar to fill
 * @param modifier Container modifier
 * @param color Color of the progress bar
 * @param shape Shape of the progress bar
 * @param animationSpec Animation to use to change the progress percentage value throught time. Tween will be used as default
 */
@Composable
fun ProgressBar(
    progressValue: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = ShapeDefaults.Medium,
    animationSpec: AnimationSpec<Float> = tween(1200),
) {
    var targetValue by remember { mutableFloatStateOf(0F) }

    LaunchedEffect(progressValue) {
        targetValue = progressValue
    }

    val animatedValue by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "Progress bar value animation"
    )

    Box(
        modifier = modifier
            .clip(shape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedValue)
                .clip(shape)
                .background(color)
        )
    }
}


@PreviewLightDark
@Composable
private fun ProgressBarPreview() {
    ArrugarqTheme {
        Surface {
            ProgressBar(
                progressValue = 0.7f,
            )
        }
    }
}
