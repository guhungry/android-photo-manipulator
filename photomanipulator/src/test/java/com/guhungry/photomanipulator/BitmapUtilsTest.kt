package com.guhungry.photomanipulator

import android.graphics.*
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.MockAndroidFactory
import com.guhungry.photomanipulator.factory.TestHelpers.mockPointF
import com.guhungry.photomanipulator.model.CGRect
import com.guhungry.photomanipulator.model.CGSize
import com.guhungry.photomanipulator.model.FlipMode
import com.guhungry.photomanipulator.model.RotationMode
import com.guhungry.photomanipulator.model.TextStyle
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.*

internal class BitmapUtilsTest {
    @Test
    fun `printText should skip shadow when shadow color is null`() {
        val style = TextStyle(555, 45f)
        val background = mock(Bitmap::class.java)
        val location = mockPointF(99f, 74f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "shadowColor = null", location, style, factory)

        assertNoTextShadow(paint)
    }

    @Test
    fun `printText should set shadow when shadow color 888`() {
        val style = TextStyle(555, 45f, shadowColor = 888)
        val background = mock(Bitmap::class.java)
        val location = mockPointF(99f, 74f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "shadowColor = null", location, style, factory)

        verify(paint, times(1)).setShadowLayer(0f, 0f, 0f, 888)
    }

    @Test
    fun `printText should set shadow when all shadow values`() {
        val style = TextStyle(555, 45f, shadowRadius = 1f, shadowOffsetX = 2f, shadowOffsetY = 3f, shadowColor = 123)
        val background = mock(Bitmap::class.java)
        val location = mockPointF(99f, 74f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "shadowColor = null", location, style, factory)

        verify(paint, times(1)).setShadowLayer(1f, 2f, 3f, 123)
    }

    private fun assertNoTextShadow(paint: Paint) {
        verify(paint, never()).setShadowLayer(anyFloat(), anyFloat(), anyFloat(), anyInt())
    }

    @Test
    fun `printText should skip process when text is empty or blank`() {
        val style = TextStyle(555, 45f)
        val background = mock(Bitmap::class.java)
        val location = mockPointF(99f, 74f)
        val factory = mock(AndroidFactory::class.java)

        BitmapUtils.printText(background, "", location, style, factory)
        BitmapUtils.printText(background, "     ", location, style, factory)

        verify(factory, never()).makeCanvas(background)
        verify(factory, never()).makePaint()
    }

    @Test
    fun `printText should skip setTextBorder when text thickness equal 0`() {
        val style = TextStyle(555, 45f, thickness = 0f)
        val background = mock(Bitmap::class.java)
        val location = mockPointF(99f, 74f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "border = 0", location, style, factory)

        assertNoTextBorder(paint)
    }

    @Test
    fun `printText should skip setTextBorder when text thickness less than  0`() {
        val style = TextStyle(555, 45f, thickness = -100f)
        val background = mock(Bitmap::class.java)
        val location = mockPointF(99f, 74f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "border < 0", location, style, factory)

        assertNoTextBorder(paint)
    }

    @Test
    fun `printText should draw correctly without alignment use default AlignLEFT`() {
        val background = mock(Bitmap::class.java)
        val location = mockPointF(99f, 74f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(
            background,
            "Text no alignment",
            location,
            555,
            45f,
            factory = factory
        )

        verify(paint, times(1)).textAlign = Paint.Align.LEFT
    }

    @Test
    fun `printText should draw correctly without thickness use default thickness 0`() {
        val background = mock(Bitmap::class.java)
        val location = mockPointF(99f, 74f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(
            background,
            "Text no Thickness",
            location,
            555,
            45f,
            factory = factory
        )

        assertNoTextBorder(paint)
    }

    @Test
    fun `printText should draw correctly without font use default font`() {
        val background = mock(Bitmap::class.java)
        val location = mockPointF(99f, 74f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(
            background,
            "Text no Thickness",
            location,
            555,
            45f,
            factory = factory
        )

        assertNoFont(paint)
    }

    private fun assertNoFont(paint: Paint) {
        verify(paint, never()).typeface = any()
    }

    private fun assertNoTextBorder(paint: Paint) {
        verify(paint, never()).strokeWidth = anyFloat()
        verify(paint, never()).style = any()
    }

    @Test
    fun `printText should draw correctly with all values`() {
        val background = mock(Bitmap::class.java)
        val location = mockPointF(23f, 14f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        val font = mock(Typeface::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "Text all Values", location, 432, 74f, font, Paint.Align.CENTER, 4f, 45f, factory)

        verify(paint, times(1)).color = 432
        verify(paint, times(1)).textSize = 74f
        verify(paint, times(1)).textAlign = Paint.Align.CENTER
        verify(paint, times(1)).style = Paint.Style.STROKE
        verify(paint, times(1)).typeface = font
        verify(paint, times(1)).strokeWidth = 4f
        verify(canvas, times(1)).drawText("Text all Values", 23f, 51f, paint)
        verify(canvas, times(1)).rotate(-45f, 23f, 51f)
    }

    @Test
    fun `printText when rotation null should rotate with 0`() {
        val background = mock(Bitmap::class.java)
        val location = mockPointF(69f, 55f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        val font = mock(Typeface::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "rotation null", location, 772, 84f, font, Paint.Align.RIGHT, 0f, null, factory)

        verify(paint, times(1)).color = 772
        verify(paint, times(1)).textSize = 84f
        verify(paint, times(1)).textAlign = Paint.Align.RIGHT
        verify(paint, times(1)).typeface = font
        verify(canvas, times(1)).drawText("rotation null", 69f, 97f, paint)
        verify(canvas, times(1)).rotate(-0f, 69f, 97f)
    }

    @Test
    fun `overlay should draw image correctly`() {
        val background = mock(Bitmap::class.java)
        val overlay = mock(Bitmap::class.java)
        val location = mockPointF(75f, 95f)
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.overlay(background, overlay, location, factory)

        verify(paint, times(1)).xfermode = any<PorterDuffXfermode>()
        verify(canvas, times(1)).drawBitmap(overlay, 75f, 95f, paint)
    }

    @Test
    fun `flip when FlipMode None do nothing`() {
        val background = mock(Bitmap::class.java)

        val actual = BitmapUtils.flip(background, FlipMode.None)

        assertThat(actual, sameInstance(background))
    }

    @Test
    fun `rotate when RotationMode None do nothing`() {
        val background = mock(Bitmap::class.java)

        val actual = BitmapUtils.rotate(background, RotationMode.None)

        assertThat(actual, sameInstance(background))
    }

    /**
     * Error due to calculate origin y = -1 instead of 0
     * When
     * cropRegion: {"x":0,"y":0,"height":4025,"width":3060}
     * targetSize: {"height":4025,"width":3060}
     * https://github.com/guhungry/react-native-photo-manipulator/issues/837
     */
    @Test
    fun `findCropPosition when issue 837 should return correctly`() {
        val factory = MockAndroidFactory()
        val expected = CGRect(0, 0, 3060, 4025, factory)
        val cropRegion = CGRect(0, 0, 3060, 4025, factory)
        val targetSize = CGSize(3060, 4025)
        val sampleSize = 1

        val actual = BitmapUtils.findCropPosition(cropRegion, targetSize, sampleSize, factory)

        assertThat(actual.size, equalTo(expected.size))
        assertThat(actual.origin.x, equalTo(expected.origin.x))
        assertThat(actual.origin.y, equalTo(expected.origin.y))
    }
}