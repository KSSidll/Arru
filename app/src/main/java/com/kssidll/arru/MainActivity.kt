package com.kssidll.arru

import android.content.Intent
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
import androidx.core.os.BundleCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.detectDarkMode
import com.kssidll.arru.data.preference.getColorScheme
import com.kssidll.arru.data.preference.getCurrencyFormatLocale
import com.kssidll.arru.data.preference.getDynamicColor
import com.kssidll.arru.data.preference.getPersistentNotificationsEnabled
import com.kssidll.arru.data.preference.setResettableToDefault
import com.kssidll.arru.service.DataExportService
import com.kssidll.arru.service.PersistentNotificationService
import com.kssidll.arru.service.getServiceStateCold
import com.kssidll.arru.service.setServiceState
import com.kssidll.arru.ui.theme.ArrugarqTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import java.util.Locale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val LocalCurrencyFormatLocale = compositionLocalOf { Locale.getDefault() }

const val BUNDLE_NAV_CONTROLLER = "NAV_CONTROLLER"
const val INTENT_NAVIGATE_TO_KEY = "intent_navigate_to_key"
const val INTENT_NAVIGATE_TO_ADD_TRANSACTION = "intent_navigate_to_add_transaction"

inline fun <reified T> getClassJava(): Class<T> = T::class.java

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var mNavController: NavController<Screen>? = null

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
                applicationContext.getServiceStateCold(DataExportService::class.java),
            )

            applicationContext.setServiceState(
                PersistentNotificationService.SERVICE_NAME,
                applicationContext.getServiceStateCold(PersistentNotificationService::class.java),
            )
            if (AppPreferences.getPersistentNotificationsEnabled(applicationContext).first()) {
                PersistentNotificationService.start(applicationContext)
            }

            colorScheme = AppPreferences.getColorScheme(applicationContext).first()
            isInDynamicColor = AppPreferences.getDynamicColor(applicationContext).first()
        }

        val destination = intent.getStringExtra(INTENT_NAVIGATE_TO_KEY)
        val initialBackstack =
            if (destination == INTENT_NAVIGATE_TO_ADD_TRANSACTION) {
                listOf(Screen.Home, Screen.AddTransaction())
            } else listOf(Screen.Home)

        val navController =
            if (savedInstanceState != null) {
                BundleCompat.getParcelable(
                    savedInstanceState,
                    BUNDLE_NAV_CONTROLLER,
                    getClassJava<NavController<Screen>>(),
                ) ?: navController(initialBackstack)
            } else navController(initialBackstack)
        mNavController = navController

        setContent {
            val appColorScheme =
                AppPreferences.getColorScheme(applicationContext).collectAsState(colorScheme).value

            ArrugarqTheme(
                appColorScheme = appColorScheme,
                isInDynamicColor =
                    AppPreferences.getDynamicColor(applicationContext)
                        .collectAsState(isInDynamicColor)
                        .value,
            ) {
                enableEdgeToEdge(
                    statusBarStyle =
                        SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                            appColorScheme.detectDarkMode(),
                        ),
                    navigationBarStyle =
                        SystemBarStyle.auto(
                            Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                            Color.argb(0x80, 0x1b, 0x1b, 0x1b),
                            appColorScheme.detectDarkMode(),
                        ),
                )

                val isExpandedScreen =
                    calculateWindowSizeClass(activity = this).widthSizeClass ==
                        WindowWidthSizeClass.Expanded

                Surface(modifier = Modifier.fillMaxSize()) {
                    CompositionLocalProvider(
                        LocalCurrencyFormatLocale provides
                            AppPreferences.getCurrencyFormatLocale(applicationContext)
                                .collectAsState(Locale.getDefault())
                                .value
                    ) {
                        Navigation(
                            isExpandedScreen = isExpandedScreen,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (mNavController != null) {
            outState.putParcelable(BUNDLE_NAV_CONTROLLER, mNavController)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val destination = intent.getStringExtra(INTENT_NAVIGATE_TO_KEY)

        if (destination == INTENT_NAVIGATE_TO_ADD_TRANSACTION) {
            mNavController?.navigate(Screen.AddTransaction())
        }
    }
}
