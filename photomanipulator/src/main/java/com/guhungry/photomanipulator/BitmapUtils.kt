package com.guhungry.photomanipulator

import android.graphics.*
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.AndroidConcreteFactory
import java.io.IOException
import java.io.InputStream

object BitmapUtils {
    @JvmStatic
    fun readImageDimensions(input: InputStream): CGSize {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }

        BitmapFactory.decodeStream(input, null, options)

        return CGSize(options.outWidth, options.outHeight)
    }

    /**
     * When scaling down the bitmap, decode only every n-th pixel in each dimension.
     * Calculate the largest {@code inSampleSize} value that is a power of 2 and keeps both
     * {@code width, height} larger or equal to {@code targetWidth, targetHeight}.
     * This can significantly reduce memory usage.
     */
    private fun decodeSampleSize(sourceSize: CGSize, targetSize: CGSize): Int {
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
    @JvmStatic
    fun crop(input: InputStream, region: CGRect, outOptions: BitmapFactory.Options): Bitmap {
        input.use {
            // Efficiently crops image without loading full resolution into memory
            // https://developer.android.com/reference/android/graphics/BitmapRegionDecoder.html
            val decoder = BitmapRegionDecoder.newInstance(input, false)
            try {
                return decoder.decodeRegion(region.toRect(), outOptions)
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
    @JvmStatic
    fun cropAndResize(input: InputStream, cropSize: CGRect, targetSize: CGSize, outOptions: BitmapFactory.Options): Bitmap {
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
        val scaleMatrix = findCropScale(crop, targetSize)

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
    private fun applyScale(value: Float, sampleSize: Int) = kotlin.math.floor(value / sampleSize).toInt()

    private fun findCropScale(rect: CGRect, targetSize: CGSize): Matrix {
        val cropRectRatio = rect.size.ratio()
        val targetRatio = targetSize.ratio()

        val cropScale = if (cropRectRatio > targetRatio) { // e.g. source is landscape, target is portrait
            targetSize.height / rect.size.height.toFloat()
        } else { // e.g. source is landscape, target is portrait
            targetSize.width / rect.size.width.toFloat()
        }
        return Matrix().apply { setScale(cropScale, cropScale) }
    }

    /**
     * Print text in to image
     *
     * @param image Source image
     * @param position Position of text in image
     * @param color Color of text
     * @param size Text size
     * @param alignment Text alignment
     */
    @JvmStatic
    @JvmOverloads
    fun printText(image: Bitmap, text: String, position: PointF, color: Int, size: Float, alignment: Paint.Align = Paint.Align.LEFT, thickness: Float = 0f, factory: AndroidFactory = AndroidConcreteFactory()) {
        val canvas = factory.makeCanvas(image)

        val paint = factory.makePaint().apply {
            this.color = color
            textSize = size
            textAlign = alignment

            if (thickness > 0) {
                style = Paint.Style.STROKE
                strokeWidth = thickness
            }
        }
        canvas.drawText(text, position.x, position.y + (size / 2), paint)
    }

    /**
     * Overlay image over background
     */
    @JvmStatic
    @JvmOverloads
    fun overlay(background: Bitmap, overlay: Bitmap, position: PointF, factory: AndroidFactory = AndroidConcreteFactory()) {
        val canvas = factory.makeCanvas(background)

        val paint = factory.makePaint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        }
        canvas.drawBitmap(overlay, position.x, position.y, paint)
    }
}