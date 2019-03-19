package com.guhungry.imageeditor

import android.graphics.BitmapRegionDecoder
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Rect
import java.io.InputStream

object BitmapUtils {
    /**
     * When scaling down the bitmap, decode only every n-th pixel in each dimension.
     * Calculate the largest {@code inSampleSize} value that is a power of 2 and keeps both
     * {@code width, height} larger or equal to {@code targetWidth, targetHeight}.
     * This can significantly reduce memory usage.
     */
    fun decodeSampleSize(width: Int, height: Int, targetWidth: Int, targetHeight: Int): Int {
        var sampleSize = 1

        if (height > targetHeight || width > targetWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfWidth / sampleSize) >= targetWidth && (halfHeight / sampleSize) >= targetHeight) {
                sampleSize *= 2
            }
        }
        return sampleSize
    }

    /**
     * Reads and crops the bitmap.
     * @param outOptions Bitmap options, useful to determine `outMimeType`.
     */
    private fun crop(input: InputStream, region: Rect, outOptions: BitmapFactory.Options): Bitmap {
        input.use {
            // Efficiently crops image without loading full resolution into memory
            // https://developer.android.com/reference/android/graphics/BitmapRegionDecoder.html
            val decoder = BitmapRegionDecoder.newInstance(input, false)
            try {
                return decoder.decodeRegion(region, outOptions)
            } finally {
                decoder.recycle()
            }
        }
    }
}