package com.guhungry.photomanipulator

import android.graphics.Bitmap
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MimeUtilsTest {
    @Test
    fun `image mime type should have correct value`() {
        assertThat(MimeUtils.JPEG, equalTo("image/jpeg"))
        assertThat(MimeUtils.PNG, equalTo("image/png"))
        assertThat(MimeUtils.WEBP, equalTo("image/webp"))
    }


    @Test
    fun `toExtension should return correct file extension`() {
        assertThat(MimeUtils.toExtension(MimeUtils.JPEG), equalTo(".jpg"))
        assertThat(MimeUtils.toExtension(MimeUtils.PNG), equalTo(".png"))
        assertThat(MimeUtils.toExtension(MimeUtils.WEBP), equalTo(".webp"))
    }

    @Test
    fun `toCompresFormat should return correct CompressFormat`() {
        assertThat(MimeUtils.toCompresFormat(MimeUtils.JPEG), equalTo(Bitmap.CompressFormat.JPEG))
        assertThat(MimeUtils.toCompresFormat(MimeUtils.PNG), equalTo(Bitmap.CompressFormat.PNG))
        assertThat(MimeUtils.toCompresFormat(MimeUtils.WEBP), equalTo(Bitmap.CompressFormat.WEBP))
    }
}