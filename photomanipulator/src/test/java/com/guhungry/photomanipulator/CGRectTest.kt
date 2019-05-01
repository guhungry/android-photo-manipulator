package com.guhungry.photomanipulator

import android.graphics.Point
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class CGRectTest {
    var sut: CGRect? = null
    var origin: Point? = null

    @AfterEach
    fun tearDown() {
        sut = null
        origin = null
    }

    @Test
    fun `get origin size should have correct value`() {
        origin = Mockito.mock(Point::class.java)
        sut = CGRect(origin!!, CGSize(1255, 188))

        assertThat(sut!!.origin, equalTo(origin!!))
        assertThat(sut!!.size, equalTo(CGSize(1255, 188)))
    }
}