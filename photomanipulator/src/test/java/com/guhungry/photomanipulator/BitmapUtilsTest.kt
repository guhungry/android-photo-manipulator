package com.guhungry.photomanipulator

import android.graphics.*
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.MockAndroidFactory
import com.guhungry.photomanipulator.model.CGRect
import com.guhungry.photomanipulator.model.CGSize
import com.guhungry.photomanipulator.model.FlipMode
import com.guhungry.photomanipulator.model.RotationMode
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.*

internal class BitmapUtilsTest {
    @Test
    fun `printText should draw correctly without alignment and thickness`() {
        val background = mock(Bitmap::class.java)
        val location = PointF().apply {
            x = 99f
            y = 74f
        }
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "Text no Thickness", location, 555, 45f, factory = factory)

        verify(paint, times(1)).color = 555
        verify(paint, times(1)).textSize = 45f
        verify(paint, times(1)).textAlign = Paint.Align.LEFT
        verify(canvas, times(1)).drawText("Text no Thickness", 99f, 96.5f, paint)
        verify(paint, never()).strokeWidth = anyFloat()
        verify(paint, never()).setTypeface(any())
        verify(paint, never()).style = any()
    }

    @Test
    fun `printText should draw correctly with all values`() {
        val background = mock(Bitmap::class.java)
        val location = PointF().apply {
            x = 23f
            y = 14f
        }
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
        verify(paint, times(1)).setTypeface(font)
        verify(paint, times(1)).strokeWidth = 4f
        verify(canvas, times(1)).drawText("Text all Values", 23f, 51f, paint)
        verify(canvas, times(1)).rotate(-45f, 23f, 51f)
    }

    @Test
    fun `printText when rotation null should rotate with 0`() {
        val background = mock(Bitmap::class.java)
        val location = PointF().apply {
            x = 69f
            y = 55f
        }
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        val font = mock(Typeface::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.printText(background, "rotation null", location, 772, 84f, font, Paint.Align.RIGHT, 0f, null, factory)

        verify(paint, times(1)).setColor(772)
        verify(paint, times(1)).textSize = 84f
        verify(paint, times(1)).textAlign = Paint.Align.RIGHT
        verify(paint, times(1)).setTypeface(font)
        verify(canvas, times(1)).drawText("rotation null", 69f, 97f, paint)
        verify(canvas, times(1)).rotate(-0f, 69f, 97f)
    }

    @Test
    fun `overlay should draw image correctly`() {
        val background = mock(Bitmap::class.java)
        val overlay = mock(Bitmap::class.java)
        val location = PointF().apply {
            x = 75f
            y = 95f
        }
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        val factory = mock(AndroidFactory::class.java)
        `when`(factory.makeCanvas(background)).thenReturn(canvas)
        `when`(factory.makePaint()).thenReturn(paint)

        BitmapUtils.overlay(background, overlay, location, factory)

        verify(paint, times(1)).setXfermode(any<PorterDuffXfermode>())
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