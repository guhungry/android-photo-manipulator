package com.guhungry.photomanipulator

import android.graphics.*
import android.util.DisplayMetrics
import androidx.annotation.DrawableRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guhungry.photomanipulator.test.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class BitmapUtilsAndroidTest {
    private var background: Bitmap? = null
    private var overlay: Bitmap? = null
    private var output: Bitmap? = null

    @After
    fun tearDown() {
        background?.recycle()
        background = null
        overlay?.recycle()
        overlay = null
        output?.recycle()
        output = null
    }

    @Test
    fun crop_should_have_correct_size() {
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.crop(it, CGRect(79, 45, 32, 96), BitmapFactory.Options())

            assertThat(output!!.width, equalTo(32))
            assertThat(output!!.height, equalTo(96))
        }
    }

    @Test
    fun cropAndResize_when_portrait_should_have_correct_size() {
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(19, 48), BitmapFactory.Options())

            assertThat(output!!.width, equalTo(19))
            assertThat(output!!.height, equalTo(48))
        }
    }

    @Test
    fun cropAndResize_when_landscape_should_have_correct_size() {
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(15, 48), BitmapFactory.Options())

            assertThat(output!!.width, equalTo(15))
            assertThat(output!!.height, equalTo(48))
        }
    }

    @Test
    fun getCorrectOrientationMatrix_should_return_correctly() {
        val matrix = FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.issue_exif)).use {
            BitmapUtils.getCorrectOrientationMatrix(it)
        }
        val actual = FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.issue_exif)).use {
            BitmapUtils.cropAndResize(it, CGRect(400, 45, 32, 96), CGSize(19, 48), BitmapFactory.Options(), matrix)
                .getPixel(1, 1)
        }
        val original = FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            BitmapUtils.cropAndResize(it, CGRect(400, 45, 32, 96), CGSize(19, 48), BitmapFactory.Options())
                .getPixel(1, 1)
        }
        val bad = FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.issue_exif)).use {
            BitmapUtils.cropAndResize(it, CGRect(400, 45, 32, 96), CGSize(19, 48), BitmapFactory.Options())
                .getPixel(1, 1)
        }
        assertThat(actual, equalTo(original))
        assertThat(actual, not(equalTo(bad)))
    }

    @Test
    fun overlay_should_overlay_image_at_correct_location() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        overlay = TestHelper.drawableBitmap(R.drawable.overlay, options)

        BitmapUtils.overlay(background!!, overlay!!, PointF(75f, 145f))

        assertThat(background!!.colorSpace, equalTo(overlay!!.colorSpace))
        assertThat(background!!.getPixel(75 + 96, 145 + 70), equalTo(overlay!!.getPixel(96, 70)))
    }

    @Test
    fun printText_when_some_values_should_text_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        BitmapUtils.printText(background!!, "My Text Print", PointF(12f, 5f), Color.GREEN, 23f)

        assertThat(background!!.getPixel(14, 5), equalTo(Color.GREEN))
    }

    @Test
    fun printText_when_all_values_should_text_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        BitmapUtils.printText(background!!, "My Text Print", PointF(12f, 5f), Color.GREEN, 23f, Typeface.DEFAULT_BOLD, thickness = 1f)

        assertThat(background!!.getPixel(14, 0), equalTo(Color.GREEN))
    }

    @Test
    fun printText_when_multiline_should_text_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        BitmapUtils.printText(background!!, "My Text\nPrint", PointF(12f, 5f), Color.GREEN, 23f, Typeface.DEFAULT_BOLD, thickness = 5f)
        BitmapUtils.printText(background!!, "My\nText\nPrint", PointF(200f, 5f), Color.YELLOW, 23f, Typeface.DEFAULT_BOLD, thickness = 1f, alignment = Paint.Align.CENTER)

        assertThat(background!!.getPixel(14, 0), equalTo(Color.GREEN))
    }

    @Test
    fun flip_when_horizontal_should_flip_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        val actual = BitmapUtils.flip(background!!, FlipMode.Horizontal)

        assertThat(actual.getPixel(55, 67), equalTo(0xFF0B5BA2.toInt()))
    }

    @Test
    fun flip_when_vertical_should_flip_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        val actual = BitmapUtils.flip(background!!, FlipMode.Vertical)

        assertThat(actual.getPixel(55, 67), equalTo(0xFF072957.toInt()))
    }

    @Test
    fun flip_when_both_should_flip_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        val actual1 = BitmapUtils.flip(background!!, FlipMode.Vertical)
        val actual2 = BitmapUtils.flip(actual1, FlipMode.Horizontal)
        val actual3 = BitmapUtils.flip(actual2, FlipMode.Vertical)
        val actual4 = BitmapUtils.flip(actual3, FlipMode.Horizontal)

        assertThat(actual4.getPixel(55, 67), equalTo(0xFF118BCE.toInt()))
    }

    /**
     * Fix Issue For
     * https://github.com/react-native-community/react-native-image-editor/issues/27
     */
    @Test
    fun cropAndResize_when_bug_scaledown_should_have_correct_size() {
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.issue27)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(0, 0, 2160, 3840), CGSize(16, 16), BitmapFactory.Options())

            assertThat(output!!.width, equalTo(16))
            assertThat(output!!.height, equalTo(16))
        }
    }

    /**
     * Must handle orientation in EXIF data correctly
     */
    @Test
    fun openBitmapInputStream_must_when_no_orientation_should_do_nothing() {
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            val actual = BitmapUtils.getCorrectOrientationMatrix(it)

            assertThat(actual, nullValue())
        }
    }

    @Test
    fun openBitmapInputStream_must_when_orientation_should_return_correctly() {
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.issue_exif)).use {
            val actual = BitmapUtils.getCorrectOrientationMatrix(it)

            assertThat(actual, not(equalTo(Matrix())))
        }
    }

    @Test
    fun readImageDimensions_should_return_correct_dimension() {
        assertReadImageDimensions(R.drawable.background, 800, 530)
        assertReadImageDimensions(R.drawable.overlay, 200, 141)
    }

    private fun assertReadImageDimensions(@DrawableRes res: Int, width: Int, height: Int) {
        val file = TestHelper.drawableUri(res)
        FileUtils.openBitmapInputStream(TestHelper.context(), file).use {
            val size = BitmapUtils.readImageDimensions(it)
            assertThat(size, equalTo(CGSize(width, height)))
        }
    }
}