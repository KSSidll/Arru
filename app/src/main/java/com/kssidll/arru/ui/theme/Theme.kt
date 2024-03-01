package com.kssidll.arru.ui.theme

import android.os.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.colorspace.*
import androidx.compose.ui.platform.*
import com.google.accompanist.systemuicontroller.*
import com.kssidll.arru.ui.theme.schema.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*
import kotlin.math.*

const val disabledAlpha = 0.38f
const val optionalAlpha = 0.60f
val colorSpace = ColorSpaces.Srgb

/**
 * Sets navigation bar color to [color]
 * @param color Color to set the navigation bar color to
 * @param darkIcons Whether to use dark icons
 * @param systemUiController [SystemUiController] instance, sets the navigation bar color
 */
fun setNavigationBarColor(
    color: Color,
    darkIcons: Boolean,
    systemUiController: SystemUiController,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        systemUiController.setNavigationBarColor(
            color = color,
            darkIcons = !darkIcons
        )
    } else {
        // Icon colors on versions lower than Q appear to be inverse of what we set them
        // te be
        systemUiController.setNavigationBarColor(
            color = color,
            darkIcons = darkIcons
        )
    }
}

/**
 * Sets status bar color to [color]
 * @param color Color to set the status bar color to
 * @param darkIcons Whether to use dark icons
 * @param systemUiController [SystemUiController] instance, sets the status bar color
 */
fun setStatusBarColor(
    color: Color,
    darkIcons: Boolean,
    systemUiController: SystemUiController,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = !darkIcons
        )
    } else {
        // Icon colors on versions lower than Q appear to be inverse of what we set them
        // te be
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = darkIcons
        )
    }
}

/**
 * @return Color scheme to use
 * @param darkTheme Whether the color scheme should be a dark theme one
 * @param dynamicColor Whether to use dynamic color to build the color scheme
 */
@Composable
fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
): ColorScheme {
    return when {
        // dynamic color is available since API 31
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // dark theme toggle option is available since API 29, so we default to it on lower API version
        darkTheme || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> DarkColorScheme

        else -> LightColorScheme
    }
}

/**
 * @return Whether the app is considered to be in dark theme
 */
@Composable
fun isAppInDarkTheme(): Boolean {
    return isSystemInDarkTheme()
}

/**
 * @return Whether the app should use dynamic color to build the color scheme
 */
@Composable
fun isAppInDynamicColor(): Boolean {
    return true
}

/**
 * @return Default application chart style
 */
@Composable
fun arrugarqChartStyle(): ChartStyle {
    return m3ChartStyle(
        entityColors = List(12) { itr ->
            val main = MaterialTheme.colorScheme.tertiary
            val red = main.red
            val green = main.green
            val blue = main.blue
            val alpha = main.alpha
            val colorSpace = main.colorSpace

            val minRed = max(
                red.minus(0.25f),
                0f
            )
            val minGreen = max(
                green.minus(0.25f),
                0f
            )
            val minBlue = max(
                blue.minus(0.25f),
                0f
            )

            val maxRed = min(
                red.plus(0.25f),
                1f
            )
            val maxGreen = min(
                green.plus(0.25f),
                1f
            )
            val maxBlue = min(
                blue.plus(0.25f),
                1f
            )

            val newRed = red.plus(0.11f * itr)
                .minus(minRed)
                .mod(maxRed - minRed)
                .plus(minRed)
            val newGreen = green.plus(0.8f * itr)
                .minus(minGreen)
                .mod(maxGreen - minGreen)
                .plus(minGreen)
            val newBlue = blue.plus(0.5f * itr)
                .minus(minBlue)
                .mod(maxBlue - minBlue)
                .plus(minBlue)

            Color(
                red = newRed,
                green = newGreen,
                blue = newBlue,
                alpha = alpha,
                colorSpace = colorSpace,
            )
        }
    )
}

/**
 * Default application theme
 * @param content Content to provide the theme to
 */
@Composable
fun ArrugarqTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isAppInDarkTheme()
    val view = LocalView.current
    val systemUiController = rememberSystemUiController()
    val colorScheme = getColorScheme(
        darkTheme = darkTheme,
        dynamicColor = isAppInDynamicColor(),
    )

    SideEffect {
        if (!view.isInEditMode) {
            setStatusBarColor(
                color = colorScheme.surfaceContainer,
                darkIcons = darkTheme,
                systemUiController = systemUiController,
            )

            setNavigationBarColor(
                color = colorScheme.surfaceContainer,
                darkIcons = darkTheme,
                systemUiController = systemUiController,
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        ProvideChartStyle(
            chartStyle = arrugarqChartStyle()
        ) {
            content()
        }
    }
}