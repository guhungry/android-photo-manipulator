package com.guhungry.photomanipulator

import android.graphics.*
import com.guhungry.photomanipulator.factory.AndroidFactory
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class BitmapUtilsTest {

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
        val helper = mock(AndroidFactory::class.java)
        `when`(helper.makeCanvas(background)).thenReturn(canvas)
        `when`(helper.makePaint()).thenReturn(paint)

        BitmapUtils.overlay(background, overlay, location, helper)

        verify(paint, times(1)).setXfermode(any<PorterDuffXfermode>())
        verify(canvas, times(1)).drawBitmap(overlay, 75f, 95f, paint)
    }
}