package com.guhungry.photomanipulator

import android.graphics.Bitmap.CompressFormat
import android.os.Build

object MimeUtils {
    const val PNG: String = "image/png"
    const val WEBP: String = "image/webp"
    const val JPEG: String = "image/jpeg"

    @JvmStatic
    fun toExtension(type: String?): String = when (type) {
        PNG -> ".png"
        WEBP -> ".webp"
        else -> ".jpg"
    }

    @JvmStatic
    fun toCompressFormat(type: String): CompressFormat {
        val formatWEBP =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                CompressFormat.WEBP_LOSSY
            } else {
                // Should be removed if min sdk >= 30
                @Suppress("DEPRECATION") CompressFormat.WEBP
            }
        return when (type) {
            PNG -> CompressFormat.PNG
            WEBP -> formatWEBP
            else -> CompressFormat.JPEG
        }
    }
}