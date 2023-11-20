package com.kssidll.arrugarq

import android.annotation.*
import android.content.pm.*
import android.os.*
import androidx.activity.compose.*
import androidx.appcompat.app.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.*
import com.kssidll.arrugarq.domain.preference.*
import com.kssidll.arrugarq.ui.theme.*
import dagger.hilt.android.*
import kotlinx.coroutines.*
import javax.inject.*

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    @Inject
    lateinit var preferences: Preferences

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        //!! Lock orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        runBlocking {
            preferences.setNullToDefault(applicationContext)
        }

        setContent {
            ArrugarqTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation()
                }
            }
        }
    }
}
