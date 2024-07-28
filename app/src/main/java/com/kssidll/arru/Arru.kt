package com.kssidll.arru

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess

@HiltAndroidApp
class Arru: Application() {

    companion object {
        /**
         * restarts the app
         * @param context app context
         */
        fun restart(context: Context) {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
            exitProcess(0)
        }
    }
}

const val DAY_IN_MILIS: Long = 86400000

@Preview(
    group = "Expanded",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_FOLD
)
@Preview(
    group = "Expanded",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO,
    device = Devices.PIXEL_FOLD
)
annotation class PreviewExpanded