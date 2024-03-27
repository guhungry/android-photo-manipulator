package com.guhungry.photomanipulator.factory

import android.graphics.*
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidFactoryAndroidTest {
    var sut: AndroidFactory? = null
    var bitmap: Bitmap? = null

    @Before
    fun setUp() {
        sut = AndroidConcreteFactory()
        bitmap = Bitmap.createBitmap(1, 2, Bitmap.Config.ARGB_8888)
    }

    @After
    fun tearDown() {
        sut = null
        bitmap!!.recycle()
        bitmap = null
    }

    @Test
    fun makePoint_When5n8_ShouldReturnPoint5n8() {
        val actual = sut!!.makePoint(5, 8)

        assertThat(actual, equalTo(Point(5, 8)))
    }

    @Test
    fun makeRect_When1n2n3n4_ShouldReturnRect1n2n3n4() {
        val actual = sut!!.makeRect(1, 2, 3, 4)

        assertThat(actual, equalTo(Rect(1, 2, 3, 4)))
    }

    @Test
    fun makeUri_WhenHelloBakaCom_ShouldReturnUriHelloBakaCom() {
        val actual = sut!!.makeUri("hello://baka.com")

        assertThat(actual, equalTo(Uri.parse("hello://baka.com")))
    }

    @Test
    fun makeCanvas_WhenBitmap1n2_ShouldReturnCanvas1n2() {
        val actual = sut!!.makeCanvas(bitmap!!)

        assertThat(actual, instanceOf(Canvas::class.java))
        assertThat(actual.width, equalTo(1))
        assertThat(actual.height, equalTo(2))
    }

    @Test
    fun makePaint_ShouldReturnPaint() {
        val actual = sut!!.makePaint()

        assertThat(actual, instanceOf(Paint::class.java))
    }
}