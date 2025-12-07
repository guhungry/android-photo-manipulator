package com.guhungry.photomanipulator

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guhungry.photomanipulator.model.CGRect
import com.guhungry.photomanipulator.test.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class BitmapUtilsCropTest {
    private var output: Bitmap? = null

    @After
    fun tearDown() {
        output?.recycle()
        output = null
    }

    @Test
    fun crop_when_region_fully_inside_image_should_crop_correctly() {
        // Arrange: Use background.jpg image
        // The crop region is fully within the image bounds
        val cropRegion = CGRect(50, 50, 100, 100)
        val options = BitmapFactory.Options()

        // Act
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.crop(it, cropRegion, options)
        }

        // Assert: Verify the cropped bitmap has the correct dimensions
        assertThat(output, notNullValue())
        assertThat(output!!.width, equalTo(100))
        assertThat(output!!.height, equalTo(100))
    }

    @Test
    fun crop_when_region_partially_inside_image_should_crop_available_area() {
        // Arrange: Use background.jpg image
        // Get the image dimensions first to create a partially overlapping region
        val imageDimensions = FileUtils.openBitmapInputStream(
            TestHelper.context(),
            TestHelper.drawableUri(R.drawable.background)
        ).use {
            BitmapUtils.readImageDimensions(it)
        }

        // Create a crop region that extends beyond the image bounds
        // The region starts near the right edge and extends beyond it
        val cropX = imageDimensions.width - 50
        val cropY = imageDimensions.height - 50
        val cropRegion = CGRect(cropX, cropY, 100, 100)
        val options = BitmapFactory.Options()

        // Act
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.crop(it, cropRegion, options)
        }

        // Assert: Verify the cropped bitmap is limited to the available area
        // The actual cropped size should be less than the requested size
        assertThat(output, notNullValue())
        assertThat(output!!.width, lessThanOrEqualTo(100))
        assertThat(output!!.height, lessThanOrEqualTo(100))
        assertThat(output!!.width, greaterThan(0))
        assertThat(output!!.height, greaterThan(0))
    }

    @Test
    fun crop_when_region_fully_outside_image_should_return_empty_or_throw() {
        // Arrange: Use background.jpg image
        // Get the image dimensions first
        val imageDimensions = FileUtils.openBitmapInputStream(
            TestHelper.context(),
            TestHelper.drawableUri(R.drawable.background)
        ).use {
            BitmapUtils.readImageDimensions(it)
        }

        // Create a crop region completely outside the image bounds
        val cropRegion = CGRect(
            imageDimensions.width + 100,
            imageDimensions.height + 100,
            50,
            50
        )
        val options = BitmapFactory.Options()

        // Act & Assert
        // BitmapRegionDecoder typically returns null or throws when region is fully outside
        try {
            FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
                output = BitmapUtils.crop(it, cropRegion, options)
            }

            // If no exception is thrown, the output should be null or have zero dimensions
            if (output != null) {
                assertThat(output!!.width, equalTo(0))
                assertThat(output!!.height, equalTo(0))
            } else {
                assertThat(output, nullValue())
            }
        } catch (e: IllegalArgumentException) {
            // This is expected behavior when the region is completely outside
            assertThat(e, notNullValue())
        }
    }
}

