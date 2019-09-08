package com.guhungry.photomanipulator.helper

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.core.StringEndsWith
import org.hamcrest.core.StringStartsWith
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class AndroidFileAndroidTest {
    var sut: AndroidFile? = null
    var context: Context? = null

    @Before
    fun setUp() {
        sut = AndroidConcreteFile()
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @After
    fun tearDown() {
        sut = null
        context = null
    }

    @Test
    fun createTempFile() {
        val path = context!!.cacheDir

        val actual = sut!!.createTempFile("BEE_PREFIX", ".ext", path)
        actual.deleteOnExit()

        assertThat(actual.absolutePath, StringStartsWith(path.absolutePath))
        assertThat(actual.name, StringStartsWith("BEE_PREFIX"))
        assertThat(actual.name, StringEndsWith(".ext"))
    }

    @Test
    fun makeFileOutputStream() {
        val file = sut!!.createTempFile("BEE_PREFIX", ".ext", context!!.cacheDir)
        file.deleteOnExit()

        sut!!.makeFileOutputStream(file).use {
            assertThat(it, instanceOf(FileOutputStream::class.java))
        }
    }
}