package com.kssidll.arru

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.Preferences
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.detectDarkMode
import com.kssidll.arru.data.preference.getColorScheme
import com.kssidll.arru.data.preference.getCurrencyFormatLocale
import com.kssidll.arru.data.preference.getDynamicColor
import com.kssidll.arru.data.preference.setResettableToDefault
import com.kssidll.arru.service.DataExportService
import com.kssidll.arru.service.getServiceStateCold
import com.kssidll.arru.service.setServiceState
import com.kssidll.arru.ui.theme.ArrugarqTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

val LocalCurrencyFormatLocale = compositionLocalOf { Locale.getDefault() }

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    @Inject
    lateinit var preferences: Preferences

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        var colorScheme: AppPreferences.Theme.ColorScheme.Values
        var isInDynamicColor: Boolean

        runBlocking {
            AppPreferences.setResettableToDefault(applicationContext)

            applicationContext.setServiceState(
                DataExportService.SERVICE_NAME,
                applicationContext.getServiceStateCold(DataExportService::class.java)
            )

            colorScheme = AppPreferences.getColorScheme(applicationContext).first()
            isInDynamicColor = AppPreferences.getDynamicColor(applicationContext).first()
        }


        setContent {
            val appColorScheme =
                AppPreferences.getColorScheme(applicationContext).collectAsState(colorScheme).value

            ArrugarqTheme(
                appColorScheme = appColorScheme,
                isInDynamicColor = AppPreferences.getDynamicColor(applicationContext)
                    .collectAsState(isInDynamicColor).value
            ) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                        appColorScheme.detectDarkMode()
                    ),
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                        Color.argb(0x80, 0x1b, 0x1b, 0x1b),
                        appColorScheme.detectDarkMode()
                    )
                )

                val isExpandedScreen =
                    calculateWindowSizeClass(activity = this).widthSizeClass == WindowWidthSizeClass.Expanded

                Surface(modifier = Modifier.fillMaxSize()) {
                    CompositionLocalProvider(
                        LocalCurrencyFormatLocale provides AppPreferences.getCurrencyFormatLocale(
                            applicationContext
                        ).collectAsState(Locale.getDefault()).value
                    ) {
                        Navigation(isExpandedScreen)
                    }
                }
            }
        }
    }
}