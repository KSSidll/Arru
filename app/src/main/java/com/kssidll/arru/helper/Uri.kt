package com.kssidll.arru.helper

import android.net.Uri

fun getReadablePathFromUri(uri: Uri): String {
    val path = uri.path
    if (path != null) {
        // Extract the part after "/tree/"
        val treePath = path.substringAfter("/tree/", "")
        if (treePath.isNotEmpty()) {
            return treePath.replace(':', '/').substringAfterLast("//")
        }
    }

    // If all else fails, return the URI as a string
    return uri.toString()
}