package com.guhungry.photomanipulator

import android.content.Context
import com.guhungry.photomanipulator.helper.AndroidFile
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.io.File
import java.io.FilenameFilter
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
        context = mock(Context::class.java)

        val exception = assertThrows<IOException> { FileUtils.cachePath(context!!) }

        assertThat(exception, instanceOf(IOException::class.java))
        assertThat(exception.message, equalTo("No cache directory available"))
    }

    @Test
    fun `cachePath should externalCacheDir when internalCacheDir is null`() {
        val dir = mock(File::class.java)
        context = mock(Context::class.java)
        `when`(context!!.externalCacheDir).thenReturn(dir)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(dir))
    }

    @Test
    fun `cachePath should internalCacheDir when externalCacheDir is null`() {
        val dir = mock(File::class.java)
        context = mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(dir)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(dir))
    }

    @Test
    fun `cachePath should externalCacheDir when external has more free space`() {
        val external = mock(File::class.java)
        `when`(external.freeSpace).thenReturn(55555)
        val internal = mock(File::class.java)
        `when`(internal.freeSpace).thenReturn(30000)

        context = mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(internal)
        `when`(context!!.externalCacheDir).thenReturn(external)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(external))
    }

    @Test
    fun `cachePath should internalCacheDir when external has less free space`() {
        val external = mock(File::class.java)
        `when`(external.freeSpace).thenReturn(1111)
        val internal = mock(File::class.java)
        `when`(internal.freeSpace).thenReturn(30000)

        context = mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(internal)
        `when`(context!!.externalCacheDir).thenReturn(external)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(internal))
    }

    @Test
    fun `cleanDirectory should delete file with starts with prefix`() {
        val directory = mock(File::class.java)
        val files = arrayOf(mock(File::class.java))
        `when`(directory.listFiles(any<FilenameFilter>())).thenReturn(files)

        FileUtils.cleanDirectory(directory, "DELETE ME")

        verify(files[0], times(1)).delete()
    }

    @Test
    fun `createTempFile should return temp file correctly`() {
        val helper = mock(AndroidFile::class.java)
        val internal = mock(File::class.java)
        `when`(internal.freeSpace).thenReturn(30000)

        context = mock(Context::class.java)
        `when`(context!!.cacheDir).thenReturn(internal)

        FileUtils.createTempFile(context!!, "PREFIX", MimeUtils.JPEG, helper)

        verify(helper, times(1)).createTempFile("PREFIX", ".jpg", internal)
    }
}