package com.guhungry.photomanipulator

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.graphics.PointF
import android.util.DisplayMetrics
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.guhungry.photomanipulator.test.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class BitmapUtilsAndroidTest {
    var background: Bitmap? = null
    var overlay: Bitmap? = null

    @After
    fun tearDown() {
        background?.recycle()
        background = null
        overlay?.recycle()
        overlay = null
    }

    @Test
    fun overlay_should_overlay_image_at_correct_location() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = BitmapFactory.decodeResource(context.resources, R.drawable.background, options)
        overlay = BitmapFactory.decodeResource(context.resources, R.drawable.overlay, options)

        BitmapUtils.overlay(background!!, overlay!!, PointF(75f, 145f))

        assertThat(background!!.colorSpace, equalTo(overlay!!.colorSpace))
        assertThat(background!!.getPixel(75 + 96, 145 + 70), equalTo(overlay!!.getPixel(96, 70)))
    }
}