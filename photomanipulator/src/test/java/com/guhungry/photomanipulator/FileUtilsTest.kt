package com.guhungry.photomanipulator

import android.content.Context
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.io.File
import java.io.IOException

internal class FileUtilsTest {
    private var context: Context? = null

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
        context = null
    }

    @Test
    fun `cachePath should throw error when all cache dir is null`() {
        context = Mockito.mock(Context::class.java)

        val exception = assertThrows<IOException> { FileUtils.cachePath(context!!) }

        assertThat(exception, instanceOf(IOException::class.java))
        assertThat(exception.message, equalTo("No cache directory available"))
    }

    @Test
    fun `cachePath should externalCacheDir when internalCacheDir is null`() {
        val dir = Mockito.mock(File::class.java)
        context = Mockito.mock(Context::class.java)
        `when`(context!!.externalCacheDir).thenReturn(dir)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(dir))
    }

    @Test
    fun `cachePath should internalCacheDir when externalCacheDir is null`() {
        val dir = Mockito.mock(File::class.java)
        context = Mockito.mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(dir)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(dir))
    }

    @Test
    fun `cachePath should externalCacheDir when external has more free space`() {
        val external = Mockito.mock(File::class.java)
        `when`(external.freeSpace).thenReturn(55555)
        val internal = Mockito.mock(File::class.java)
        `when`(internal.freeSpace).thenReturn(30000)

        context = Mockito.mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(internal)
        `when`(context!!.externalCacheDir).thenReturn(external)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(external))
    }

    @Test
    fun `cachePath should internalCacheDir when external has less free space`() {
        val external = Mockito.mock(File::class.java)
        `when`(external.freeSpace).thenReturn(1111)
        val internal = Mockito.mock(File::class.java)
        `when`(internal.freeSpace).thenReturn(30000)

        context = Mockito.mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(internal)
        `when`(context!!.externalCacheDir).thenReturn(external)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(internal))
    }
}