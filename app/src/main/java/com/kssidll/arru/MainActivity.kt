package com.kssidll.arru

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.*
import androidx.compose.ui.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.*
import com.kssidll.arru.data.preference.*
import com.kssidll.arru.ui.theme.*
import dagger.hilt.android.*
import kotlinx.coroutines.*
import javax.inject.*

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    @Inject
    lateinit var preferences: Preferences

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        runBlocking {
            preferences.setNullToDefault(applicationContext)
        }

        enableEdgeToEdge()

        setContent {
            ArrugarqTheme {
                val isExpandedScreen =
                    calculateWindowSizeClass(activity = this).widthSizeClass == WindowWidthSizeClass.Expanded

                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation(isExpandedScreen)
                }
            }
        }
    }
}
