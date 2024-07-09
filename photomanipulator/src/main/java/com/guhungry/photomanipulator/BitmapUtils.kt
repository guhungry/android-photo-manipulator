package com.guhungry.photomanipulator

import android.graphics.*
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.AndroidConcreteFactory
import com.guhungry.photomanipulator.model.CGRect
import com.guhungry.photomanipulator.model.CGSize
import com.guhungry.photomanipulator.model.FlipMode
import com.guhungry.photomanipulator.model.RotationMode
import com.guhungry.photomanipulator.model.TextStyle
import java.io.IOException
import java.io.InputStream
import kotlin.math.floor

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
                return decoder.decodeRegion(region.toRect(), outOptions)
            } finally {
                decoder.recycle()
            }
        }
    }

    private fun getBitmapRegionDecoder(input: InputStream) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            BitmapRegionDecoder.newInstance(input)!!
        } else {
            // Should be removed if min sdk >= 31
            BitmapRegionDecoder.newInstance(input, false)!!
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
    internal fun findCropPosition(
        rect: CGRect,
        targetSize: CGSize,
        sampleSize: Int,
        factory: AndroidFactory = AndroidConcreteFactory()
    ): CGRect {
        val newWidth: Float
        val newHeight: Float
        val newX: Float
        val newY: Float
        val cropRectRatio = rect.size.ratio()
        val targetRatio = targetSize.ratio()

        if (cropRectRatio > targetRatio) { // e.g. source is landscape, target is portrait
            newWidth = floor(rect.size.height * targetRatio)
            newHeight = rect.size.height.toFloat()
            newX = rect.origin.x + (rect.size.width - newWidth) / 2
            newY = rect.origin.y.toFloat()
        } else { // e.g. source is landscape, target is portrait
            newWidth = rect.size.width.toFloat()
            newHeight = floor(rect.size.width / targetRatio)
            newX = rect.origin.x.toFloat()
            newY = rect.origin.y + (rect.size.height - newHeight) / 2
        }

        return CGRect(applyScale(newX, sampleSize), applyScale(newY, sampleSize), applyScale(newWidth, sampleSize), applyScale(newHeight, sampleSize), factory)
    }
    private fun applyScale(value: Float, sampleSize: Int) = floor(value / sampleSize).toInt()

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
    @Deprecated("Use printText(Bitmap, String, PointF, TextStyle, AndroidFactory) instead")
    fun printText(image: Bitmap, text: String, position: PointF, color: Int, size: Float, font: Typeface? = null, alignment: Paint.Align = Paint.Align.LEFT, thickness: Float = 0f, rotation: Float? = null, factory: AndroidFactory = AndroidConcreteFactory()) {
        val style = TextStyle(color, size, font, alignment, thickness, rotation)
        return printText(image, text, position, style, factory)
    }

    /**
     * Print text in to image
     *
     * @param image Source image
     * @param text Text to be printed
     * @param position Position of text in image
     * @param textStyle Text style
     * @param factory (Optional) Factory for creating Android Objects for Testing
     */
    @JvmStatic
    @JvmOverloads
    fun printText(image: Bitmap, text: String, position: PointF, textStyle: TextStyle, factory: AndroidFactory = AndroidConcreteFactory()) {
        if (text.isBlank()) return

        val canvas = factory.makeCanvas(image)
        val paint = factory.makePaint().apply {
            color = textStyle.color
            textSize = textStyle.size
            textAlign = textStyle.alignment
            isAntiAlias = true

            textStyle.font?.let {
                typeface = it
            }

            setTextBorder(textStyle)
            setTextShadow(textStyle)
        }

        var offset = position.y + (textStyle.size / 2)
        // Rotate
        canvas.save()
        canvas.rotate(-(textStyle.rotation ?: 0f), position.x, offset)
        // Draw Text
        text.split("\n").forEach {
            canvas.drawText(it, position.x, offset, paint)
            offset += paint.descent() - paint.ascent()
        }
        canvas.restore()
    }

    private fun Paint.setTextBorder(textStyle: TextStyle) {
        if (textStyle.thickness <= 0) return
        style = Paint.Style.STROKE
        strokeWidth = textStyle.thickness
    }

    private fun Paint.setTextShadow(textStyle: TextStyle) {
        if (textStyle.shadowColor == null) return
        setShadowLayer(
            textStyle.shadowRadius,
            textStyle.shadowOffsetX,
            textStyle.shadowOffsetY,
            textStyle.shadowColor
        )
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

    /**
     * Flip image horizontal or vertical
     * @param image Image to be flipped
     * @param mode Flip Mode
     */
    @JvmStatic
    fun flip(image: Bitmap, mode: FlipMode): Bitmap {
        if (mode == FlipMode.None) return image
        val matrix = Matrix()
        matrix.preScale(mode.scaleX, mode.scaleY)
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true)
    }

    /**
     * Rotate image 90, 180, 270 degrees
     * @param image Image to be rotated
     * @param mode Rotation Mode
     */
    @JvmStatic
    fun rotate(image: Bitmap, mode: RotationMode): Bitmap {
        if (mode == RotationMode.None) return image
        val matrix = Matrix()
        matrix.preRotate(mode.degrees)
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true)
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