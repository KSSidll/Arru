package com.kssidll.arru.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.ui.theme.schema.DarkColorScheme
import com.kssidll.arru.ui.theme.schema.LightColorScheme
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.VicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import kotlin.math.max
import kotlin.math.min

const val disabledAlpha = 0.38f
const val optionalAlpha = 0.60f

/**
 * @param darkTheme Whether the color scheme should be a dark theme one
 * @param dynamicColor Whether to use dynamic color to build the color scheme
 * @return Color scheme to use
 */
@Composable
fun getColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme {
    return when {
        // dynamic color is available since API 31
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
}

/** @return Whether the app is considered to be in dark theme */
@Composable
fun isAppInDarkTheme(colorScheme: AppPreferences.Theme.ColorScheme.Values): Boolean {
    return when (colorScheme) {
        AppPreferences.Theme.ColorScheme.Values.SYSTEM -> isSystemInDarkTheme()
        AppPreferences.Theme.ColorScheme.Values.DARK -> true
        AppPreferences.Theme.ColorScheme.Values.LIGHT -> false
    }
}

/** @return Default application chart style */
@Composable
fun arruChartTheme(): VicoTheme {
    return rememberM3VicoTheme(
        lineCartesianLayerColors =
            List(12) { itr ->
                val main = MaterialTheme.colorScheme.primary
                val red = main.red
                val green = main.green
                val blue = main.blue
                val alpha = main.alpha
                val colorSpace = main.colorSpace

                val minRed = max(red.minus(0.25f), 0f)
                val minGreen = max(green.minus(0.25f), 0f)
                val minBlue = max(blue.minus(0.25f), 0f)

                val maxRed = min(red.plus(0.25f), 1f)
                val maxGreen = min(green.plus(0.25f), 1f)
                val maxBlue = min(blue.plus(0.25f), 1f)

                val newRed = red.plus(0.11f * itr).minus(minRed).mod(maxRed - minRed).plus(minRed)
                val newGreen =
                    green.plus(0.8f * itr).minus(minGreen).mod(maxGreen - minGreen).plus(minGreen)
                val newBlue =
                    blue.plus(0.5f * itr).minus(minBlue).mod(maxBlue - minBlue).plus(minBlue)

                Color(
                    red = if (itr > 0) newRed else red,
                    green = if (itr > 0) newGreen else green,
                    blue = if (itr > 0) newBlue else blue,
                    alpha = alpha,
                    colorSpace = colorSpace,
                )
            }
    )
}

/**
 * Default application theme
 *
 * @param content Content to provide the theme to
 */
@Composable
fun ArrugarqTheme(
    appColorScheme: AppPreferences.Theme.ColorScheme.Values =
        AppPreferences.Theme.ColorScheme.DEFAULT,
    isInDynamicColor: Boolean = AppPreferences.Theme.DynamicColor.DEFAULT,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        getColorScheme(
            darkTheme = isAppInDarkTheme(appColorScheme),
            dynamicColor = isInDynamicColor,
        )

    @Suppress("AnimateAsStateLabel")
    val animatedColorScheme =
        ColorScheme(
            primary = animateColorAsState(colorScheme.primary, tween(300)).value,
            onPrimary = animateColorAsState(colorScheme.onPrimary, tween(300)).value,
            primaryContainer = animateColorAsState(colorScheme.primaryContainer, tween(300)).value,
            onPrimaryContainer =
                animateColorAsState(colorScheme.onPrimaryContainer, tween(300)).value,
            inversePrimary = animateColorAsState(colorScheme.inversePrimary, tween(300)).value,
            secondary = animateColorAsState(colorScheme.secondary, tween(300)).value,
            onSecondary = animateColorAsState(colorScheme.onSecondary, tween(300)).value,
            secondaryContainer =
                animateColorAsState(colorScheme.secondaryContainer, tween(300)).value,
            onSecondaryContainer =
                animateColorAsState(colorScheme.onSecondaryContainer, tween(300)).value,
            tertiary = animateColorAsState(colorScheme.tertiary, tween(300)).value,
            onTertiary = animateColorAsState(colorScheme.onTertiary, tween(300)).value,
            tertiaryContainer =
                animateColorAsState(colorScheme.tertiaryContainer, tween(300)).value,
            onTertiaryContainer =
                animateColorAsState(colorScheme.onTertiaryContainer, tween(300)).value,
            background = animateColorAsState(colorScheme.background, tween(300)).value,
            onBackground = animateColorAsState(colorScheme.onBackground, tween(300)).value,
            surface = animateColorAsState(colorScheme.surface, tween(300)).value,
            onSurface = animateColorAsState(colorScheme.onSurface, tween(300)).value,
            surfaceVariant = animateColorAsState(colorScheme.surfaceVariant, tween(300)).value,
            onSurfaceVariant = animateColorAsState(colorScheme.onSurfaceVariant, tween(300)).value,
            surfaceTint = animateColorAsState(colorScheme.surfaceTint, tween(300)).value,
            inverseSurface = animateColorAsState(colorScheme.inverseSurface, tween(300)).value,
            inverseOnSurface = animateColorAsState(colorScheme.inverseOnSurface, tween(300)).value,
            error = animateColorAsState(colorScheme.error, tween(300)).value,
            onError = animateColorAsState(colorScheme.onError, tween(300)).value,
            errorContainer = animateColorAsState(colorScheme.errorContainer, tween(300)).value,
            onErrorContainer = animateColorAsState(colorScheme.onErrorContainer, tween(300)).value,
            outline = animateColorAsState(colorScheme.outline, tween(300)).value,
            outlineVariant = animateColorAsState(colorScheme.outlineVariant, tween(300)).value,
            scrim = animateColorAsState(colorScheme.scrim, tween(300)).value,
            surfaceBright = animateColorAsState(colorScheme.surfaceBright, tween(300)).value,
            surfaceDim = animateColorAsState(colorScheme.surfaceDim, tween(300)).value,
            surfaceContainer = animateColorAsState(colorScheme.surfaceContainer, tween(300)).value,
            surfaceContainerHigh =
                animateColorAsState(colorScheme.surfaceContainerHigh, tween(300)).value,
            surfaceContainerHighest =
                animateColorAsState(colorScheme.surfaceContainerHighest, tween(300)).value,
            surfaceContainerLow =
                animateColorAsState(colorScheme.surfaceContainerLow, tween(300)).value,
            surfaceContainerLowest =
                animateColorAsState(colorScheme.surfaceContainerLowest, tween(300)).value,
        )

    MaterialTheme(colorScheme = animatedColorScheme, typography = Typography) {
        ProvideVicoTheme(theme = arruChartTheme()) { content() }
    }
}
