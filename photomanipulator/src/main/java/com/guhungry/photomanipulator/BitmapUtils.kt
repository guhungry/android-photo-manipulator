package com.guhungry.photomanipulator

import android.graphics.*
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.AndroidConcreteFactory
import com.guhungry.photomanipulator.model.CGRect
import com.guhungry.photomanipulator.model.CGSize
import com.guhungry.photomanipulator.model.FlipMode
import com.guhungry.photomanipulator.model.ResizeMode
import com.guhungry.photomanipulator.model.RotationMode
import com.guhungry.photomanipulator.model.TextStyle
import java.io.IOException
import java.io.InputStream
import kotlin.math.floor
import androidx.core.graphics.withRotation

object BitmapUtils {
    /**
     * Read image dimensions without loading the full image into memory.
     *
     * Note: This method does not close the input stream. The caller is responsible for closing it.
     *
     * @param input Image input stream
     * @return CGSize containing width and height of the image
     */
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
     *
     * Note: This method does not close the input stream. The caller is responsible for closing it.
     *
     * @param outOptions Bitmap options, useful to determine `outMimeType`.
     */
    @JvmStatic
    fun crop(input: InputStream, region: CGRect, outOptions: BitmapFactory.Options): Bitmap {
        // Efficiently crops image without loading full resolution into memory
        // https://developer.android.com/reference/android/graphics/BitmapRegionDecoder.html
        val decoder = getBitmapRegionDecoder(input)
        try {
            return decoder.decodeRegion(region.toRect(), outOptions)
        } finally {
            decoder.recycle()
        }
    }

    private fun getBitmapRegionDecoder(input: InputStream): BitmapRegionDecoder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            BitmapRegionDecoder.newInstance(input)
                ?: throw IOException("Failed to create BitmapRegionDecoder from input stream")
        } else {
            // Should be removed if min sdk >= 31
            @Suppress("DEPRECATION")
            BitmapRegionDecoder.newInstance(input, false)
                ?: throw IOException("Failed to create BitmapRegionDecoder from input stream")
        }
    }

    /**
     * Crop the rectangle given by {@code mX, mY, mWidth, mHeight} within the source bitmap
     * and scale the result to {@code targetWidth, targetHeight}.
     *
     * Note: This method does not close the input stream. The caller is responsible for closing it.
     *
     * @param outOptions Bitmap options, useful to determine {@code outMimeType}.
     * @param mode Resize mode (Cover, Contain, or Stretch). Default is Cover for backward compatibility.
     * @param matrix Transformation for correct orientation from {@code #}
     */
    @JvmStatic
    @JvmOverloads
    fun cropAndResize(input: InputStream, cropSize: CGRect, targetSize: CGSize, outOptions: BitmapFactory.Options, matrix: Matrix? = null, mode: ResizeMode = ResizeMode.Cover): Bitmap {
        // Loading large bitmaps efficiently:
        // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html

        // Decode the bitmap. We have to open the stream again, like in the example linked above.
        // Is there a way to just continue reading from the stream?
        outOptions.inSampleSize = decodeSampleSize(cropSize.size, targetSize)
        outOptions.inJustDecodeBounds = false

        val bitmap: Bitmap = BitmapFactory.decodeStream(input, null, outOptions) 
            ?: throw IOException("Failed to decode bitmap from input stream")
        // This can use significantly less memory than decoding the full-resolution bitmap

        val rotated = transformBitmap(bitmap, matrix)

        // Apply resize mode to determine crop position and scale
        val crop = findCropPosition(cropSize, targetSize, outOptions.inSampleSize, mode)
        val scaleMatrix = findCropScale(crop, targetSize, mode)

        return Bitmap.createBitmap(rotated, crop.origin.x, crop.origin.y, crop.size.width, crop.size.height, scaleMatrix, true).also {
            if (it != rotated) rotated.recycle()
        }
    }

    private fun transformBitmap(bitmap: Bitmap, matrix: Matrix?): Bitmap {
        return if (matrix == null) bitmap else Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true).also {
            if (it != bitmap) bitmap.recycle()
        } // No transformation, use original bitmap
    }

    /**
     * Find Crop Position - dispatches to mode-specific implementation
     */
    private fun findCropPosition(
        rect: CGRect,
        targetSize: CGSize,
        sampleSize: Int,
        mode: ResizeMode,
        factory: AndroidFactory = AndroidConcreteFactory()
    ): CGRect {
        return when (mode) {
            ResizeMode.Cover -> findCropPositionCover(rect, targetSize, sampleSize, factory)
            ResizeMode.Contain, ResizeMode.Stretch -> findCropPositionStretchOrContain(rect, sampleSize, factory)
        }
    }

    /**
     * Find Crop Scale - dispatches to mode-specific implementation
     */
    private fun findCropScale(rect: CGRect, targetSize: CGSize, mode: ResizeMode): Matrix {
        return when (mode) {
            ResizeMode.Cover -> findCropScaleCover(rect, targetSize)
            ResizeMode.Contain -> findCropScaleContain(rect, targetSize)
            ResizeMode.Stretch -> findCropScaleStretch(rect, targetSize)
        }
    }

    /**
     * Find Crop Position for Cover mode - scales to fill, crops excess
     */
    internal fun findCropPositionCover(
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

    /**
     * Find Crop Position for Stretch or Contain mode - no cropping, use full rectangle
     */
    private fun findCropPositionStretchOrContain(
        rect: CGRect,
        sampleSize: Int,
        factory: AndroidFactory = AndroidConcreteFactory()
    ): CGRect {
        return CGRect(
            applyScale(rect.origin.x.toFloat(), sampleSize),
            applyScale(rect.origin.y.toFloat(), sampleSize),
            applyScale(rect.size.width.toFloat(), sampleSize),
            applyScale(rect.size.height.toFloat(), sampleSize),
            factory
        )
    }

    private fun applyScale(value: Float, sampleSize: Int) = floor(value / sampleSize).toInt()

    /**
     * Find Crop Scale for Cover mode - scale to fill target size
     */
    private fun findCropScaleCover(rect: CGRect, targetSize: CGSize): Matrix {
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
     * Find Crop Scale for Contain mode - scale to fit within target size, maintaining aspect ratio
     */
    private fun findCropScaleContain(rect: CGRect, targetSize: CGSize): Matrix {
        val scaleX = targetSize.width / rect.size.width.toFloat()
        val scaleY = targetSize.height / rect.size.height.toFloat()
        // Use the smaller scale to ensure image fits within target
        val scale = minOf(scaleX, scaleY)
        return Matrix().apply { setScale(scale, scale) }
    }

    /**
     * Find Crop Scale for Stretch mode - scale to exact target size, ignoring aspect ratio
     */
    private fun findCropScaleStretch(rect: CGRect, targetSize: CGSize): Matrix {
        val scaleX = targetSize.width / rect.size.width.toFloat()
        val scaleY = targetSize.height / rect.size.height.toFloat()
        return Matrix().apply { setScale(scaleX, scaleY) }
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
    @Deprecated(
        message = "Use printText(Bitmap, String, PointF, TextStyle, AndroidFactory) instead",
        replaceWith = ReplaceWith(
            expression = "printText(image, text, position, TextStyle(color, size, font, alignment, thickness, rotation), factory)",
            imports = ["com.guhungry.photomanipulator.model.TextStyle"]
        ),
        level = DeprecationLevel.WARNING
    )
    fun printText(image: Bitmap, text: String, position: PointF, color: Int, size: Float, font: Typeface? = null, alignment: Paint.Align = Paint.Align.LEFT, thickness: Float = 0f, rotation: Float? = null, factory: AndroidFactory = AndroidConcreteFactory()) {
        val style = TextStyle(color, size, font, alignment, thickness, rotation)
        return printText(image, text, position, style, factory)
    }

    /**
     * Print text in to image.
     *
     * Note: When using thickness > 0, the text will be rendered as an outline (stroke) only.
     * Shadow effects can be applied independently via TextStyle.shadowColor and shadowRadius.
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
        canvas.withRotation(-(textStyle.rotation ?: 0f), position.x, offset) {
            // Draw Text
            text.split("\n").forEach {
                drawText(it, position.x, offset, paint)
                offset += paint.descent() - paint.ascent()
            }
        }
    }

    private fun Paint.setTextBorder(textStyle: TextStyle) {
        if (textStyle.thickness <= 0) return
        style = Paint.Style.STROKE
        strokeWidth = textStyle.thickness
    }

    private fun Paint.setTextShadow(textStyle: TextStyle) {
        if (textStyle.shadowColor == null || textStyle.shadowRadius <= 0f) return
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
     * Flip image horizontal or vertical.
     *
     * Note: This method creates a new Bitmap. The caller is responsible for recycling
     * the original bitmap if it is no longer needed to avoid memory leaks.
     *
     * @param image Image to be flipped
     * @param mode Flip Mode
     * @return New flipped Bitmap (or same instance if mode is FlipMode.None)
     */
    @JvmStatic
    fun flip(image: Bitmap, mode: FlipMode): Bitmap {
        if (mode == FlipMode.None) return image
        val matrix = Matrix()
        matrix.preScale(mode.scaleX, mode.scaleY)
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true)
    }

    /**
     * Rotate image 90, 180, 270 degrees.
     *
     * Note: This method creates a new Bitmap. The caller is responsible for recycling
     * the original bitmap if it is no longer needed to avoid memory leaks.
     *
     * @param image Image to be rotated
     * @param mode Rotation Mode
     * @return New rotated Bitmap (or same instance if mode is RotationMode.None)
     */
    @JvmStatic
    fun rotate(image: Bitmap, mode: RotationMode): Bitmap {
        if (mode == RotationMode.None) return image
        val matrix = Matrix()
        matrix.preRotate(mode.degrees)
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true)
    }

    /**
     * Get corrected transform matrix for orientation data in EXIF.
     *
     * Note: This method does not close the input stream. The caller is responsible for closing it.
     *
     * @param input Image input stream
     * @return Matrix with rotation/flip transformations, or null if no correction needed
     */
    @JvmStatic
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