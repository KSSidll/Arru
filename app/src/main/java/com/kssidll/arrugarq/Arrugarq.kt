package com.kssidll.arrugarq

import android.app.*
import android.content.*
import dagger.hilt.android.*
import kotlin.system.*

@HiltAndroidApp
class Arrugarq: Application() {

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