package com.kssidll.arru.ui.component.other


import androidx.compose.animation.core.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arru.ui.theme.*

/**
 * A progress bar component
 * @param progressValue Percentage, from 0.0 to 1.0, of the progress bar to fill
 * @param modifier Container modifier
 * @param animationSpec Animation to use to change the progress percentage value throught time. Tween will be used as default
 */
@Composable
fun ProgressBar(
    progressValue: Float,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = tween(1200),
) {
    var targetValue by remember { mutableFloatStateOf(0F) }

    LaunchedEffect(progressValue) {
        targetValue = progressValue
    }

    val animatedValue = animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "Progress bar value animation"
    )

    Surface(
        modifier = modifier,
        shape = ShapeDefaults.Medium,
    ) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.tertiary,
            progress = { animatedValue.value },
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
