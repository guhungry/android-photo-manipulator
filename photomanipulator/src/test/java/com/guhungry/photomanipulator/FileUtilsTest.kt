package com.guhungry.photomanipulator

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import com.guhungry.photomanipulator.factory.MockAndroidFactory
import com.guhungry.photomanipulator.helper.AndroidFile
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.*

internal class FileUtilsTest {
    private var context: Context? = null

    @Before
    fun setUp() {
        context = mock(Context::class.java)
    }

    @After
    fun tearDown() {
        context = null
    }

    @Test
    fun `cachePath should throw error when all cache dir is null`() {
        val actual = assertThrows(IOException::class.java) { FileUtils.cachePath(context!!) }

        assertThat(actual.message, equalTo("No cache directory available"))
    }

    @Test
    fun `cachePath should externalCacheDir when internalCacheDir is null`() {
        val dir = mock(File::class.java)
        `when`(context!!.externalCacheDir).thenReturn(dir)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(dir))
    }

    @Test
    fun `cachePath should internalCacheDir when externalCacheDir is null`() {
        val dir = mock(File::class.java)
        `when`(context!!.cacheDir).thenReturn(dir)

        assertThat(FileUtils.cachePath(context!!)!!, sameInstance(dir))
    }

    @Test
    fun `cachePath should externalCacheDir when external has more free space`() {
        val external = mock(File::class.java)
        `when`(external.freeSpace).thenReturn(55555)
        val internal = mock(File::class.java)
        `when`(internal.freeSpace).thenReturn(30000)

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

        `when`(context!!.cacheDir).thenReturn(internal)

        FileUtils.createTempFile(context!!, "PREFIX", MimeUtils.JPEG, helper)

        verify(helper, times(1)).createTempFile("PREFIX", ".jpg", internal)
    }

    @Test
    fun `saveImageFile should save correct data`() {
        val image = mock(Bitmap::class.java)
        val uri = mock(File::class.java)
        val output = mock(FileOutputStream::class.java)
        val helper = mock(AndroidFile::class.java)
        `when`(helper.makeFileOutputStream(uri)).thenReturn(output)

        FileUtils.saveImageFile(image, MimeUtils.JPEG, 32, uri, helper)

        verify(image, times(1)).compress(Bitmap.CompressFormat.JPEG, 32, output)
    }

    @Test
    fun `openBitmapInputStream when local file should open local stream`() {
        val contentResolver = mock(ContentResolver::class.java)
        `when`(contentResolver.openInputStream(any())).thenReturn(mock(InputStream::class.java))
        `when`(context!!.contentResolver).thenReturn(contentResolver)
        val factory = MockAndroidFactory()

        FileUtils.openBitmapInputStream(context!!, "file://local/path/for/sure", factory)
        FileUtils.openBitmapInputStream(context!!, "content://local/content/path/for/sure", factory)
        FileUtils.openBitmapInputStream(context!!, "android.resource://local/resource/path/for/sure", factory)

        verify(contentResolver, times(3)).openInputStream(any())
    }

    @Test
    fun `openBitmapInputStream when remote file should open connection stream`() {
        val contentResolver = mock(ContentResolver::class.java)
        `when`(contentResolver.openInputStream(any())).thenReturn(mock(InputStream::class.java))
        `when`(context!!.contentResolver).thenReturn(contentResolver)
        val factory = MockAndroidFactory()

        FileUtils.openBitmapInputStream(context!!, "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/React-icon.svg/1000px-React-icon.svg.png", factory)

        verify(contentResolver, times(0)).openInputStream(any())
    }

    @Test
    fun `openBitmapInputStream when no stream should throw error`() {
        val contentResolver = mock(ContentResolver::class.java)
        `when`(contentResolver.openInputStream(any())).thenReturn(null)
        `when`(context!!.contentResolver).thenReturn(contentResolver)
        val factory = MockAndroidFactory()

        val actual = assertThrows(IOException::class.java) {
            FileUtils.openBitmapInputStream(context!!, "file://local/path/for/sure", factory)
        }

        assertThat(actual.message, equalTo("Cannot open bitmap: file://local/path/for/sure"))
    }
}