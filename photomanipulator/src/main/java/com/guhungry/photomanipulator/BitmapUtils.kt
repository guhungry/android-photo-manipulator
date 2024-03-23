package com.guhungry.photomanipulator

import android.graphics.*
import android.os.Build
import androidx.exifinterface.media.ExifInterface
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
            val decoder = getBitmapRegionDecoder(input)
            try {
                return decoder!!.decodeRegion(region.toRect(), outOptions)
            } finally {
                decoder?.recycle()
            }
        }
    }

    private fun getBitmapRegionDecoder(input: InputStream) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            BitmapRegionDecoder.newInstance(input)
        } else {
            // Should be removed if min sdk >= 31
            BitmapRegionDecoder.newInstance(input, false)
        }

    /**
     * Crop the rectangle given by {@code mX, mY, mWidth, mHeight} within the source bitmap
     * and scale the result to {@code targetWidth, targetHeight}.
     * @param outOptions Bitmap options, useful to determine {@code outMimeType}.
     * @param matrix Transformation for correct orientation from {@code #}
     */
    @JvmStatic
    @JvmOverloads
    fun cropAndResize(input: InputStream, cropSize: CGRect, targetSize: CGSize, outOptions: BitmapFactory.Options, matrix: Matrix? = null): Bitmap {
        // Loading large bitmaps efficiently:
        // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html

        // Decode the bitmap. We have to open the stream again, like in the example linked above.
        // Is there a way to just continue reading from the stream?
        outOptions.inSampleSize = decodeSampleSize(cropSize.size, targetSize)
        outOptions.inJustDecodeBounds = false

        val bitmap: Bitmap = BitmapFactory.decodeStream(input, null, outOptions) ?: throw IOException("Cannot decode bitmap: uri")
        // This can use significantly less memory than decoding the full-resolution bitmap

        val rotated = if (matrix != null) Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
            bitmap.height, matrix, true).also { bitmap.recycle()  } else bitmap

        // This uses scaling mode COVER
        // Where would the crop rect end up within the scaled bitmap?
        val crop = findCropPosition(cropSize, targetSize, outOptions.inSampleSize)
        val scaleMatrix = findCropScale(crop, targetSize)

        return Bitmap.createBitmap(rotated, crop.origin.x, crop.origin.y, crop.size.width, crop.size.height, scaleMatrix, true)
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
     * @param font Typeface (Font) to use
     * @param alignment Text alignment
     * @param thickness border thickness
     * @param rotation The amount of rotation, in degrees
     */
    @JvmStatic
    @JvmOverloads
    fun printText(image: Bitmap, text: String, position: PointF, color: Int, size: Float, font: Typeface? = null, alignment: Paint.Align = Paint.Align.LEFT, thickness: Float = 0f, rotation: Float? = null, factory: AndroidFactory = AndroidConcreteFactory()) {
        val canvas = factory.makeCanvas(image)

        val paint = factory.makePaint().apply {
            this.color = color
            textSize = size
            textAlign = alignment

            if (font != null) {
                typeface = font
            }

            if (thickness > 0) {
                style = Paint.Style.STROKE
                strokeWidth = thickness
            }
        }

        var offset = position.y + (size / 2)
        // Rotate
        canvas.save()
        canvas.rotate(-(rotation ?: 0f), position.x, offset)
        // Draw Text
        text.split("\n").forEach {
            canvas.drawText(it, position.x, offset, paint)
            offset += paint.descent() - paint.ascent()
        }
        canvas.restore()
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

    fun getCorrectOrientationMatrix(input: InputStream): Matrix? {
        val exif = ExifInterface(input)
        val isFlippedHorizontal = exif.isFlipped
        val rotationDegrees = exif.rotationDegrees

        if (!isFlippedHorizontal && rotationDegrees == 0) return null
        return Matrix().apply {
            postRotate(rotationDegrees.toFloat())
            if (isFlippedHorizontal) preScale(-1.0f, 1.0f)
        }
    }
}