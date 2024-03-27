package com.guhungry.photomanipulator.helper

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guhungry.photomanipulator.TestHelper
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringEndsWith
import org.hamcrest.core.StringStartsWith
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class AndroidFileAndroidTest {
    private var sut: AndroidFile? = null

    @Before
    fun setUp() {
        sut = AndroidConcreteFile()
    }

    @After
    fun tearDown() {
        sut = null
    }

    @Test
    fun createTempFile_ShouldReturnCorrectFileName() {
        val path = TestHelper.tempDirectory()

        val actual = sut!!.createTempFile("BEE_PREFIX", ".ext", path).apply {
            deleteOnExit()
        }

        assertThat(actual.absolutePath, StringStartsWith(path.absolutePath))
        assertThat(actual.name, StringStartsWith("BEE_PREFIX"))
        assertThat(actual.name, StringEndsWith(".ext"))
    }

    @Test
    fun makeFileOutputStream() {
        val file = sut!!.createTempFile("BEE_PREFIX", ".ext", TestHelper.tempDirectory()).apply {
            deleteOnExit()
        }

        sut!!.makeFileOutputStream(file).use {
            assertThat(it, instanceOf(FileOutputStream::class.java))
        }
    }
}