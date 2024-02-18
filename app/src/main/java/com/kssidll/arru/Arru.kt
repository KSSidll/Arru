package com.kssidll.arru

import android.app.*
import android.content.*
import android.content.res.Configuration.*
import androidx.compose.ui.tooling.preview.*
import dagger.hilt.android.*
import kotlin.system.*

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