package com.guhungry.photomanipulator

import android.graphics.Bitmap.CompressFormat
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

internal class MimeUtilsTest {
    @Test
    fun `image mime type should have correct value`() {
        assertThat(MimeUtils.JPEG, equalTo("image/jpeg"))
        assertThat(MimeUtils.PNG, equalTo("image/png"))
        assertThat(MimeUtils.WEBP, equalTo("image/webp"))
    }

    @Test
    fun `toExtension should return correct file extension`() {
        assertThat(MimeUtils.toExtension(null), equalTo(".jpg"))
        assertThat(MimeUtils.toExtension(MimeUtils.JPEG), equalTo(".jpg"))
        assertThat(MimeUtils.toExtension(MimeUtils.PNG), equalTo(".png"))
        assertThat(MimeUtils.toExtension(MimeUtils.WEBP), equalTo(".webp"))
    }

    @Test
    fun `toCompressFormat should return correct CompressFormat`() {
        assertThat(MimeUtils.toCompressFormat(MimeUtils.JPEG), equalTo(CompressFormat.JPEG))
        assertThat(MimeUtils.toCompressFormat(MimeUtils.PNG), equalTo(CompressFormat.PNG))
        // Should be removed if min sdk >= 30
        @Suppress("DEPRECATION") assertThat(MimeUtils.toCompressFormat(MimeUtils.WEBP), equalTo(
            CompressFormat.WEBP))
    }
}