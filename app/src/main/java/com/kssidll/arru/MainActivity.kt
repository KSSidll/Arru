package com.kssidll.arru

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.Preferences
import com.kssidll.arru.data.preference.setNullToDefault
import com.kssidll.arru.ui.theme.ArrugarqTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
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
