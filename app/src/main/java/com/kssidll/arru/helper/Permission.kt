package com.kssidll.arru.helper

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * Checks whether [permission] is granted
 * @param context application context
 * @param permission permission to check
 * @return whether permission is granted
 */
fun checkPermission(
    context: Context,
    permission: String
): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}
