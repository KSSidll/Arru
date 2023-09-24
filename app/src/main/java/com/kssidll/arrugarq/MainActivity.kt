package com.kssidll.arrugarq

import android.annotation.*
import android.content.pm.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import com.kssidll.arrugarq.presentation.theme.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*
import dagger.hilt.android.*

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //!! Lock orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            ArrugarqTheme {
                ProvideChartStyle(
                    chartStyle = m3ChartStyle(
                        entityColors = listOf(
                            MaterialTheme.colorScheme.tertiary,
                        )
                    )
                ) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Navigation()
                    }
                }
            }
        }
    }
}
