package com.guhungry.photomanipulator

import android.graphics.Point
import android.graphics.Rect
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.MockAndroidFactory
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class CGRectTest {
    private var sut: CGRect? = null
    private var origin: Point? = null
    private var rect: Rect? = null
    private var factory: AndroidFactory? = null

    @Before
    fun setup() {
        factory = MockAndroidFactory()
    }

    @After
    fun tearDown() {
        sut = null
        origin = null
        rect = null
        factory = null
    }

    @Test
    fun `convenience constructor`() {
        sut = CGRect(11, 24, 88, 181, factory!!)

        assertThat(sut!!, instanceOf(CGRect::class.java))
        assertThat(sut!!.origin, instanceOf(Point::class.java))
        assertThat(sut!!.origin.x, equalTo(11))
        assertThat(sut!!.origin.y, equalTo(24))
        assertThat(sut!!.size, equalTo(CGSize(88, 181)))
    }

    @Test
    fun `get origin and size should have correct value`() {
        origin = factory!!.makePoint(22,15)
        sut = CGRect(origin!!, CGSize(1255, 188), factory!!)

        assertThat(sut!!.origin.x, equalTo(22))
        assertThat(sut!!.origin.y, equalTo(15))
        assertThat(sut!!.size, equalTo(CGSize(1255, 188)))
    }

    @Test
    fun `toRect should return correct rect value`() {
        sut = CGRect(35, 123, 4455, 3333, factory!!)

        rect = sut!!.toRect()
        assertThat(rect, notNullValue())
        assertThat(rect!!.left, equalTo(35))
        assertThat(rect!!.top, equalTo(123))
        assertThat(rect!!.right, equalTo(4490))
        assertThat(rect!!.bottom, equalTo(3456))
    }
}