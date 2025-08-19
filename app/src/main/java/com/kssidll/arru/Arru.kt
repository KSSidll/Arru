package com.kssidll.arru

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getPersistentNotificationsEnabled
import com.kssidll.arru.data.preference.setResettableToDefault
import com.kssidll.arru.service.DataExportService
import com.kssidll.arru.service.PersistentNotificationService
import com.kssidll.arru.service.getServiceStateCold
import com.kssidll.arru.service.setServiceState
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class Arru : Application() {

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
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
        }
    }

    companion object {
        /**
         * restarts the app
         *
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

const val APPLICATION_NAME = "Arru"

@Preview(
    group = "Expanded",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_FOLD,
)
@Preview(
    group = "Expanded",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO,
    device = Devices.PIXEL_FOLD,
)
annotation class ExpandedPreviews
