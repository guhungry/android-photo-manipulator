package com.guhungry.photomanipulator

import com.guhungry.photomanipulator.test.R
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.core.StringEndsWith
import org.hamcrest.core.StringStartsWith
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class FileUtilsAndroidTest {
    private var context: Context? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @After
    fun tearDown() {
        context = null
    }

    @Test
    fun createTempFile_WhenPrefixSuffix_ShouldReturnTempFileCorrectly() {
        val actual = FileUtils.createTempFile(context!!, "RNMP_PFEFIX_BEE", MimeUtils.WEBP)
        actual.deleteOnExit()

        assertThat(actual.name, StringStartsWith("RNMP_PFEFIX_BEE"))
        assertThat(actual.name, StringEndsWith(".webp"))
    }

    @Test
    fun saveImageFile_ShouldIncreaseFileSize() {
        val image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val output = FileUtils.createTempFile(context!!, "TEST_SAVE_IMAGE", MimeUtils.PNG)
        output.deleteOnExit()

        assertThat(output.length(), equalTo(0L))
        FileUtils.saveImageFile(image, MimeUtils.PNG, 100, output)

        assertThat(output.length(), greaterThan(0L))
    }

    @Test
    fun openBitmapInputStream_ShouldReadDrawable() {
        val name = context!!.packageName
        val uri = Uri.parse("android.resource://${name}/${R.drawable.background}").toString()

        FileUtils.openBitmapInputStream(context!!, uri).use {
            assertThat(it, instanceOf(InputStream::class.java))
        }
    }
}