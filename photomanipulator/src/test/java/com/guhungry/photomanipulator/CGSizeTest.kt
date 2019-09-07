package com.guhungry.photomanipulator

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

internal class CGSizeTest {
    var sut: CGSize? = null
    var other: CGSize? = null

    @After
    fun tearDown() {
        sut = null
        other = null
    }

    @Test
    fun `same height and with should equal`() {
        sut = CGSize(800, 600)
        other = CGSize(800, 600)

        assertThat(sut, equalTo(other))
    }

    @Test
    fun `ratio when height not zero`() {
        sut = CGSize(800, 600)

        CGSize(222,22).equals(CGSize(800, 600))
        assertThat(sut!!.ratio(), equalTo((4 / 3.0).toFloat()))
    }

    @Test
    fun `ratio when height is zero`() {
        sut = CGSize(800, 0)

        assertThat(sut!!.ratio(), equalTo(0.toFloat()))
    }

    @Test
    fun `get height and with should correct`() {
        sut = CGSize(959, 600)

        assertThat(sut!!.width, equalTo(959))
        assertThat(sut!!.height, equalTo(600))
    }
}