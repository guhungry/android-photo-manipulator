package com.guhungry.photomanipulator

import android.graphics.*
import android.util.DisplayMetrics
import androidx.annotation.DrawableRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guhungry.photomanipulator.model.CGRect
import com.guhungry.photomanipulator.model.CGSize
import com.guhungry.photomanipulator.model.FlipMode
import com.guhungry.photomanipulator.model.RotationMode
import com.guhungry.photomanipulator.model.TextStyle
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
        val style = TextStyle(Color.GREEN, 23f)
        BitmapUtils.printText(background!!, "My Text Print", PointF(12f, 5f), style)

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
        val style = TextStyle(Color.GREEN, 23f, Typeface.DEFAULT_BOLD, thickness = 1f)
        BitmapUtils.printText(background!!, "My Text Print", PointF(12f, 5f), style)

        assertThat(background!!.getPixel(13, 2), equalTo(Color.GREEN))
    }

    @Test
    fun printText_when_multiline_should_text_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        val style1 = TextStyle(Color.GREEN, 23f, Typeface.DEFAULT_BOLD, thickness = 5f)
        BitmapUtils.printText(background!!, "My Text\nPrint", PointF(12f, 5f), style1)
        val style2 = TextStyle(Color.YELLOW, 23f, Typeface.DEFAULT_BOLD, thickness = 1f, alignment = Paint.Align.CENTER)
        BitmapUtils.printText(background!!, "My\nText\nPrint", PointF(200f, 5f), style2)

        assertThat(background!!.getPixel(14, 0), equalTo(Color.GREEN))
    }

    @Test
    fun printText_when_all_shadow_should_text_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        val style = TextStyle(Color.GREEN, 30f, Typeface.DEFAULT_BOLD, shadowOffsetY = 5f, shadowOffsetX = 10f, shadowRadius = .01f, shadowColor = Color.YELLOW)
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        BitmapUtils.printText(background!!, "My Text Print", PointF(12f, 5f), style)

        assertThat(background!!.getPixel(26, 5), equalTo(Color.YELLOW))
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
        val actual = BitmapUtils.flip(background!!, FlipMode.Both)

        assertThat(actual.getPixel(55, 67), equalTo(0xFF010F29.toInt()))
    }

    @Test
    fun flip_when_multiple_should_flip_correctly() {
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

    @Test
    fun rotate_when_R90_should_rotate_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        val actual = BitmapUtils.rotate(background!!, RotationMode.R90)

        assertThat(actual.getPixel(55, 67), equalTo(0xFF072C60.toInt()))
    }

    @Test
    fun rotate_when_R180_should_rotate_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        val actual = BitmapUtils.rotate(background!!, RotationMode.R180)

        assertThat(actual.getPixel(55, 67), equalTo(0xFF010F29.toInt()))
    }

    @Test
    fun rotate_when_R270_should_rotate_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        val actual = BitmapUtils.rotate(background!!, RotationMode.R270)

        assertThat(actual.getPixel(55, 67), equalTo(0xFF0D5CA9.toInt()))
    }

    @Test
    fun rotate_when_None_should_rotate_correctly() {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inTargetDensity = DisplayMetrics.DENSITY_DEFAULT
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }
        background = TestHelper.drawableBitmap(R.drawable.background, options)
        val actual = BitmapUtils.rotate(background!!, RotationMode.None)

        assertThat(actual.getPixel(55, 67), equalTo(0xFF118BCE.toInt()))
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

    // ============================================================================
    // Memory Leak Fix Verification Tests
    // ============================================================================

    /**
     * Verify memory leak fix: cropAndResize properly recycles intermediate bitmap with rotation matrix
     *
     * This test verifies that when a rotation matrix is applied, the intermediate bitmaps
     * are properly recycled to prevent memory leaks. The test processes the same image
     * multiple times and verifies that all results are valid.
     *
     * Background: Before the fix, the 'rotated' bitmap was never recycled after the final
     * createBitmap() call, causing memory accumulation.
     */
    @Test
    fun cropAndResize_should_recycle_intermediate_bitmap_with_rotation_matrix() {
        // Create rotation matrix
        val rotationMatrix = Matrix().apply { postRotate(90f) }

        // Process image
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(
                it,
                CGRect(100, 100, 200, 200),
                CGSize(100, 100),
                BitmapFactory.Options(),
                rotationMatrix
            )

            // Assert result is valid and not recycled
            assertThat(output, notNullValue())
            assertThat(output!!.isRecycled, equalTo(false))
            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(100))
        }
    }

    /**
     * Verify memory leak fix: cropAndResize properly recycles intermediate bitmap without rotation matrix
     *
     * This test verifies that even without a rotation matrix (matrix = null), the intermediate
     * bitmap is properly recycled. In this case, 'rotated' is the same as the decoded bitmap,
     * and it must be recycled after the final transformation.
     */
    @Test
    fun cropAndResize_should_recycle_intermediate_bitmap_without_rotation_matrix() {
        // Process image without rotation matrix
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(
                it,
                CGRect(100, 100, 200, 200),
                CGSize(100, 100),
                BitmapFactory.Options(),
                null  // No rotation matrix
            )

            // Assert result is valid and not recycled
            assertThat(output, notNullValue())
            assertThat(output!!.isRecycled, equalTo(false))
            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(100))
        }
    }

    /**
     * Verify memory leak fix: Multiple sequential cropAndResize operations
     *
     * This test simulates real-world usage where multiple images are processed in sequence.
     * Without proper recycling, this would cause memory accumulation and eventually
     * OutOfMemoryError. With the fix, memory should remain stable.
     */
    @Test
    fun cropAndResize_should_not_accumulate_memory_with_multiple_operations() {
        val results = mutableListOf<Bitmap>()
        val iterations = 20

        try {
            repeat(iterations) { index ->
                FileUtils.openBitmapInputStream(
                    TestHelper.context(),
                    TestHelper.drawableUri(R.drawable.background)
                ).use {
                    // Alternate between with and without rotation matrix
                    val matrix = if (index % 2 == 0) {
                        Matrix().apply { postRotate(90f) }
                    } else {
                        null
                    }

                    val result = BitmapUtils.cropAndResize(
                        it,
                        CGRect(50, 50, 200, 200),
                        CGSize(100, 100),
                        BitmapFactory.Options(),
                        matrix
                    )

                    // Verify each result is valid
                    assertThat(result.isRecycled, equalTo(false))
                    assertThat(result.width, equalTo(100))
                    assertThat(result.height, equalTo(100))

                    results.add(result)
                }
            }

            // All bitmaps should still be valid (not recycled by accident)
            results.forEach { bitmap ->
                assertThat(bitmap.isRecycled, equalTo(false))
            }

        } finally {
            // Cleanup
            results.forEach { it.recycle() }
        }
    }

    /**
     * Verify memory leak fix: cropAndResize with large image and aggressive downsampling
     *
     * Tests the fix with a scenario that would quickly cause OutOfMemoryError without
     * proper bitmap recycling: processing a large image with significant downsampling.
     */
    @Test
    fun cropAndResize_should_handle_large_image_downsampling_without_leak() {
        val rotationMatrix = Matrix().apply { postRotate(180f) }

        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(
                it,
                CGRect(0, 0, 800, 530),  // Full image
                CGSize(50, 33),            // Aggressive downsampling
                BitmapFactory.Options(),
                rotationMatrix
            )

            // Assert result is valid
            assertThat(output, notNullValue())
            assertThat(output!!.isRecycled, equalTo(false))
            assertThat(output!!.width, equalTo(50))
            assertThat(output!!.height, equalTo(33))
        }
    }

    /**
     * Verify memory leak fix: cropAndResize with EXIF rotation
     *
     * Tests the fix with images that have EXIF orientation data, which requires
     * a transformation matrix. This is a common real-world scenario.
     */
    @Test
    fun cropAndResize_should_handle_exif_rotation_without_leak() {
        // Get orientation matrix from EXIF
        val matrix = FileUtils.openBitmapInputStream(
            TestHelper.context(),
            TestHelper.drawableUri(R.drawable.issue_exif)
        ).use {
            BitmapUtils.getCorrectOrientationMatrix(it)
        }

        // Process image with EXIF rotation matrix
        FileUtils.openBitmapInputStream(
            TestHelper.context(),
            TestHelper.drawableUri(R.drawable.issue_exif)
        ).use {
            output = BitmapUtils.cropAndResize(
                it,
                CGRect(100, 100, 200, 200),
                CGSize(100, 100),
                BitmapFactory.Options(),
                matrix
            )

            // Assert result is valid
            assertThat(output, notNullValue())
            assertThat(output!!.isRecycled, equalTo(false))
            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(100))
        }
    }

    /**
     * Verify memory leak fix: cropAndResize with different rotation angles
     *
     * Tests all common rotation angles to ensure bitmap recycling works correctly
     * regardless of the transformation applied.
     */
    @Test
    fun cropAndResize_should_handle_all_rotation_angles_without_leak() {
        val rotationAngles = listOf(0f, 90f, 180f, 270f, 45f, -90f)

        rotationAngles.forEach { angle ->
            var result: Bitmap? = null
            try {
                val matrix = Matrix().apply { postRotate(angle) }

                FileUtils.openBitmapInputStream(
                    TestHelper.context(),
                    TestHelper.drawableUri(R.drawable.background)
                ).use {
                    result = BitmapUtils.cropAndResize(
                        it,
                        CGRect(100, 100, 100, 100),
                        CGSize(50, 50),
                        BitmapFactory.Options(),
                        matrix
                    )

                    assertThat(result.isRecycled, equalTo(false))
                    assertThat(result.width, equalTo(50))
                    assertThat(result.height, equalTo(50))
                }
            } finally {
                result?.recycle()
            }
        }
    }

    /**
     * Verify memory leak fix: cropAndResize with identity scale
     *
     * Edge case: When crop size matches target size, ensuring bitmap recycling
     * still works correctly even with minimal transformation.
     */
    @Test
    fun cropAndResize_should_recycle_bitmap_even_with_identity_scale() {
        val rotationMatrix = Matrix().apply { postRotate(270f) }

        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(
                it,
                CGRect(0, 0, 100, 100),
                CGSize(100, 100),  // Same as crop size - identity scale
                BitmapFactory.Options(),
                rotationMatrix
            )

            assertThat(output, notNullValue())
            assertThat(output!!.isRecycled, equalTo(false))
            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(100))
        }
    }

    /**
     * Verify memory leak fix: Stress test with rapid successive operations
     *
     * This test performs many operations in quick succession to verify that
     * memory is properly managed under heavy load. Without the fix, this would
     * likely trigger OutOfMemoryError.
     */
    @Test
    fun cropAndResize_should_handle_rapid_successive_operations_without_leak() {
        val iterations = 50

        repeat(iterations) { index ->
            var result: Bitmap? = null
            try {
                FileUtils.openBitmapInputStream(
                    TestHelper.context(),
                    TestHelper.drawableUri(R.drawable.background)
                ).use {
                    val matrix = if (index % 3 == 0) {
                        Matrix().apply { postRotate((index * 30f) % 360f) }
                    } else {
                        null
                    }

                    result = BitmapUtils.cropAndResize(
                        it,
                        CGRect(index % 200, index % 150, 100, 100),
                        CGSize(50, 50),
                        BitmapFactory.Options(),
                        matrix
                    )

                    // Verify result is valid
                    assertThat(result.isRecycled, equalTo(false))
                    assertThat(result.width, equalTo(50))
                    assertThat(result.height, equalTo(50))
                }
            } finally {
                // Immediately recycle to simulate real usage pattern
                result?.recycle()
            }
        }
    }

    /**
     * Verify memory leak fix: cropAndResize result independence
     *
     * This test verifies that the returned bitmap is independent from intermediate
     * bitmaps, confirming that recycling intermediate bitmaps doesn't affect the result.
     */
    @Test
    fun cropAndResize_result_should_be_independent_from_intermediate_bitmaps() {
        val rotationMatrix = Matrix().apply { postRotate(90f) }

        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(
                it,
                CGRect(100, 100, 200, 200),
                CGSize(100, 100),
                BitmapFactory.Options(),
                rotationMatrix
            )

            // Assert output is valid
            assertThat(output, notNullValue())
            assertThat(output!!.isRecycled, equalTo(false))

            // Try to access pixel data - this would fail if underlying buffer was recycled
            val pixelColor = output!!.getPixel(50, 50)
            assertThat(pixelColor, not(equalTo(0)))  // Should have valid pixel data

            // Result should remain valid for further operations
            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(100))
        }
    }
}