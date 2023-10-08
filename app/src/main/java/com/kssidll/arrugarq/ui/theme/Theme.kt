package com.kssidll.arrugarq.ui.theme

import android.os.*
import android.view.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import com.google.accompanist.systemuicontroller.*
import com.kssidll.arrugarq.ui.theme.schema.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*

const val disabledAlpha = 0.38f
const val optionalAlpha = 0.60f

fun setNavigationBarColor(
    color: Color,
    darkTheme: Boolean,
    view: View,
    systemUiController: SystemUiController,
) {
    if (!view.isInEditMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            systemUiController.setNavigationBarColor(
                color = color,
                darkIcons = !darkTheme
            )
        } else {
            // Icon colors on versions lower than Q appear to be inverse of what we set them
            // te be
            systemUiController.setNavigationBarColor(
                color = color,
                darkIcons = darkTheme
            )
        }
    }
}

fun setStatusBarColor(
    color: Color,
    darkTheme: Boolean,
    view: View,
    systemUiController: SystemUiController,
) {
    if (!view.isInEditMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            systemUiController.setStatusBarColor(
                color = color,
                darkIcons = !darkTheme
            )
        } else {
            // Icon colors on versions lower than Q appear to be inverse of what we set them
            // te be
            systemUiController.setStatusBarColor(
                color = color,
                darkIcons = darkTheme
            )
        }
    }
}

@Composable
fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
): ColorScheme {
    return when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> DarkColorScheme
        else -> LightColorScheme
    }
}

@Composable
fun isAppInDarkTheme(): Boolean {
    return isSystemInDarkTheme()
}

@Composable
fun isAppInDynamicColor(): Boolean {
    return true
}

@Composable
fun arrugarqChartStyle(): ChartStyle {
    return m3ChartStyle(
        entityColors = listOf(
            MaterialTheme.colorScheme.tertiary,
        )
    )
}

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
        setStatusBarColor(
            color = colorScheme.surfaceContainer,
            darkTheme = darkTheme,
            view = view,
            systemUiController = systemUiController,
        )

        setNavigationBarColor(
            color = colorScheme.surfaceContainer,
            darkTheme = darkTheme,
            view = view,
            systemUiController = systemUiController,
        )
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