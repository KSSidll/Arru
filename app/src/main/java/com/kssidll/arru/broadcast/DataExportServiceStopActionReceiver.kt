package com.kssidll.arru.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kssidll.arru.service.DataExportService

/** Receiver that stops the [DataExportService] */
class DataExportServiceStopActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val forced = intent?.extras?.getBoolean(DataExportService.FORCED_STOP_KEY)

        Log.d(TAG, "onReceive: received with forced = $forced")

        context?.let { DataExportService.stop(it, forced ?: false) }
    }

    companion object {
        const val TAG = "EXP_S_STOP_A"
    }
}
