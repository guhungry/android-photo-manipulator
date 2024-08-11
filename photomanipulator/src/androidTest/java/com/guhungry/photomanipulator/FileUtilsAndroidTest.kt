package com.guhungry.photomanipulator

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guhungry.photomanipulator.test.R
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.core.StringEndsWith
import org.hamcrest.core.StringStartsWith
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class FileUtilsAndroidTest {
    @Test
    fun createTempFile_WhenPrefixSuffix_ShouldReturnTempFileCorrectly() {
        val actual = FileUtils.createTempFile(TestHelper.context(), "RNMP_PFEFIX_BEE", MimeUtils.WEBP).apply {
            deleteOnExit()
        }

        assertThat(actual.name, StringStartsWith("RNMP_PFEFIX_BEE"))
        assertThat(actual.name, StringEndsWith(".webp"))
    }

    @Test
    fun saveImageFile_ShouldIncreaseFileSize() {
        val image = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val output = FileUtils.createTempFile(TestHelper.context(), "TEST_SAVE_IMAGE", MimeUtils.PNG).apply {
            deleteOnExit()
        }

        assertThat(output.length(), equalTo(0L))
        FileUtils.saveImageFile(image, MimeUtils.PNG, 100, output)

        assertThat(output.length(), greaterThan(0L))
    }

    @Test
    fun openBitmapInputStream_ShouldReadDrawable() {
        val uri = TestHelper.drawableUri(R.drawable.background)

        FileUtils.openBitmapInputStream(TestHelper.context(), uri).use {
            assertThat(it, instanceOf(InputStream::class.java))
        }
    }

    @Test
    fun openBitmapInputStream_WhenBase64_ShouldReadDrawable() {
        val uri = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAAXNSR0IArs4c6QAAAA1JREFUGFdj+L+U4T8ABu8CpCYJ1DQAAAAASUVORK5CYII="

        FileUtils.openBitmapInputStream(TestHelper.context(), uri).use {
            assertThat(it, instanceOf(InputStream::class.java))
        }
    }
}