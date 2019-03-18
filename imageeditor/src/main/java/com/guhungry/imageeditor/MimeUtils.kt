package com.guhungry.imageeditor

import android.graphics.Bitmap

object MimeUtils {
    const val PNG: String = "image/png"
    const val WEBP: String = "image/webp"
    const val JPEG: String = "image/jpeg"

    fun toExtension(type: String): String = when (type) {
        PNG -> ".png"
        WEBP -> ".webp"
        else -> ".jpg"
    }

    fun toCompresFormat(type: String): Bitmap.CompressFormat = when (type) {
        PNG -> Bitmap.CompressFormat.PNG
        WEBP -> Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.JPEG
    }
}