package com.guhungry.imageeditor

import android.graphics.*
import java.io.IOException
import java.io.InputStream

object BitmapUtils {
    /**
     * When scaling down the bitmap, decode only every n-th pixel in each dimension.
     * Calculate the largest {@code inSampleSize} value that is a power of 2 and keeps both
     * {@code width, height} larger or equal to {@code targetWidth, targetHeight}.
     * This can significantly reduce memory usage.
     */
    fun decodeSampleSize(sourceSize: CGSize, targetSize: CGSize): Int {
        var sampleSize = 1

        if (sourceSize.height > targetSize.height || sourceSize.width > targetSize.width) {
            val halfHeight = sourceSize.height / 2
            val halfWidth = sourceSize.width / 2
            while ((halfWidth / sampleSize) >= targetSize.width && (halfHeight / sampleSize) >= targetSize.height) {
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

    /**
     * Crop the rectangle given by {@code mX, mY, mWidth, mHeight} within the source bitmap
     * and scale the result to {@code targetWidth, targetHeight}.
     * @param outOptions Bitmap options, useful to determine {@code outMimeType}.
     */
    private fun cropAndResize(input: InputStream, cropSize: CGRect, targetSize: CGSize, outOptions: BitmapFactory.Options): Bitmap {
        // Loading large bitmaps efficiently:
        // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html

        // Decode the bitmap. We have to open the stream again, like in the example linked above.
        // Is there a way to just continue reading from the stream?
        outOptions.inSampleSize = decodeSampleSize(cropSize.size, targetSize)
        outOptions.inJustDecodeBounds = false

        val bitmap: Bitmap = BitmapFactory.decodeStream(input, null, outOptions) ?: throw IOException("Cannot decode bitmap: uri")
        // This can use significantly less memory than decoding the full-resolution bitmap

        // This uses scaling mode COVER
        // Where would the crop rect end up within the scaled bitmap?
        val crop = findCropPosition(cropSize, targetSize, outOptions.inSampleSize)
        val scaleMatrix = findCropScale(cropSize, targetSize, outOptions.inSampleSize)

        return Bitmap.createBitmap(bitmap, crop.origin.x, crop.origin.y, crop.size.width, crop.size.height, scaleMatrix, true)
    }

    /**
     * Find Crop Position result as Resize Mode = Cover
     */
    private fun findCropPosition(rect: CGRect, targetSize: CGSize, sampleSize: Int): CGRect {
        val newWidth: Float
        val newHeight: Float
        val newX: Float
        val newY: Float
        val cropRectRatio = rect.size.ratio()
        val targetRatio = targetSize.ratio()

        if (cropRectRatio > targetRatio) { // e.g. source is landscape, target is portrait
            newWidth = rect.size.height * targetRatio
            newHeight = rect.size.height.toFloat()
            newX = rect.origin.x + (rect.size.width - newWidth) / 2
            newY = rect.origin.y.toFloat()
        } else { // e.g. source is landscape, target is portrait
            newWidth = rect.size.width.toFloat()
            newHeight = rect.size.width / targetRatio
            newX = rect.origin.x.toFloat()
            newY = rect.origin.y + (rect.size.height - newHeight) / 2
        }

        return CGRect(applyScale(newX, sampleSize), applyScale(newY, sampleSize), applyScale(newWidth, sampleSize), applyScale(newHeight, sampleSize))
    }
    private fun applyScale(value: Float, sampleSize: Int) = Math.floor((value / sampleSize).toDouble()).toInt()

    private fun findCropScale(rect: CGRect, targetSize: CGSize, sampleSize: Int): Matrix {
        val cropRectRatio = rect.size.ratio()
        val targetRatio = targetSize.ratio()

        val scale = if (cropRectRatio > targetRatio) { // e.g. source is landscape, target is portrait
            targetSize.height / rect.size.height.toFloat()
        } else { // e.g. source is landscape, target is portrait
            targetSize.width / rect.size.width.toFloat()
        }
        val cropScale = scale * sampleSize
        return Matrix().apply { setScale(cropScale, cropScale) }
    }
}