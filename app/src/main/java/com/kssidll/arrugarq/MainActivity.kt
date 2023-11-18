package com.kssidll.arrugarq

import android.annotation.*
import android.content.pm.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.*
import com.kssidll.arrugarq.di.module.*
import com.kssidll.arrugarq.domain.preference.*
import com.kssidll.arrugarq.ui.theme.*
import dagger.hilt.android.*
import kotlinx.coroutines.*
import javax.inject.*

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    @Inject
    lateinit var preferences: Preferences

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        //!! Lock orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // TODO remove this placeholder in favor of start screen if preference not set
        if (preferences[AppPreferences.Database.key] == null) {
            runBlocking {
                applicationContext.dataStore.edit {
                    it[AppPreferences.Database.key] = AppPreferences.Database.Location.EXTERNAL
                }
            }
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
