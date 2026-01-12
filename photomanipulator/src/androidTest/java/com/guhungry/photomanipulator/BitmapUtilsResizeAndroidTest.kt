package com.guhungry.photomanipulator

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.graphics.Matrix
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guhungry.photomanipulator.model.CGRect
import com.guhungry.photomanipulator.model.CGSize
import com.guhungry.photomanipulator.model.ResizeMode
import com.guhungry.photomanipulator.test.R
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class BitmapUtilsResizeAndroidTest {
    private var output: Bitmap? = null

    @After
    fun tearDown() {
        output?.recycle()
        output = null
    }

    // ============================================================================
    // Resize Mode Tests (Moved from BitmapUtilsAndroidTest + New Tests)
    // ============================================================================

    @Test
    fun cropAndResize_when_mode_cover_portrait_should_crop_and_fill() {
        // Portrait source (32x96) to portrait target (19x48) - should match target exactly
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(19, 48), BitmapFactory.Options(), mode = ResizeMode.Cover)

            assertThat(output!!.width, equalTo(19))
            assertThat(output!!.height, equalTo(48))
        }
    }

    @Test
    fun cropAndResize_when_mode_cover_upscaling_should_fill() {
        // Upscaling: Source (32x96) -> Target (64x192)
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(64, 192), BitmapFactory.Options(), mode = ResizeMode.Cover)

            assertThat(output!!.width, equalTo(64))
            assertThat(output!!.height, equalTo(192))
        }
    }

    @Test
    fun cropAndResize_when_mode_contain_portrait_should_fit_within() {
        // Portrait source (32x96) to portrait target (50x150)
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(50, 150), BitmapFactory.Options(), mode = ResizeMode.Contain)

            assertThat(output!!.width, equalTo(50))
            assertThat(output!!.height, equalTo(150))
        }
    }

    @Test
    fun cropAndResize_when_mode_contain_wider_target_should_fit_height() {
        // Portrait source (32x96) to wider target (100x150)
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(100, 150), BitmapFactory.Options(), mode = ResizeMode.Contain)

            assertThat(output!!.width, equalTo(50))
            assertThat(output!!.height, equalTo(150))
        }
    }

    @Test
    fun cropAndResize_when_mode_contain_taller_target_should_fit_width() {
        // Portrait source (32x96) to taller target (50, 300)
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(50, 300), BitmapFactory.Options(), mode = ResizeMode.Contain)

            assertThat(output!!.width, equalTo(50))
            assertThat(output!!.height, equalTo(150))
        }
    }
    
    @Test
    fun cropAndResize_when_mode_contain_upscaling_should_fit_within() {
        // Source (32x96) -> Target Large (100x300)
        // Should scale up to 100x300
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(100, 300), BitmapFactory.Options(), mode = ResizeMode.Contain)

            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(300))
        }
    }

    @Test
    fun cropAndResize_when_mode_contain_square_source_should_fit_rect() {
        // Square Source (50x50) -> Rect Target (100x200)
        // Should fit width -> 100x100 (centered conceptually, but we check dims)
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            // Mocking a 50x50 crop from larger image
            output = BitmapUtils.cropAndResize(it, CGRect(0, 0, 50, 50), CGSize(100, 200), BitmapFactory.Options(), mode = ResizeMode.Contain)

            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(100))
        }
    }

    @Test
    fun cropAndResize_when_mode_stretch_should_match_exact_size() {
        // Landscape source (96x32) to portrait target (19x48) - should stretch to exact size
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 96, 32), CGSize(19, 48), BitmapFactory.Options(), mode = ResizeMode.Stretch)

            assertThat(output!!.width, equalTo(19))
            assertThat(output!!.height, equalTo(48))
        }
    }

    @Test
    fun cropAndResize_when_mode_stretch_landscape_should_match_exact_size() {
        // Portrait source (32x96) to landscape target (100x50) - should stretch ignoring aspect ratio
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(100, 50), BitmapFactory.Options(), mode = ResizeMode.Stretch)

            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(50))
        }
    }
    
    @Test
    fun cropAndResize_when_mode_stretch_upscaling_should_match_exact_size() {
         // Source (32x96) -> Target (100x200)
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(79, 45, 32, 96), CGSize(100, 200), BitmapFactory.Options(), mode = ResizeMode.Stretch)

            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(200))
        }
    }

    @Test
    fun cropAndResize_when_mode_cover_with_matrix_should_work_correctly() {
        val rotationMatrix = Matrix().apply { postRotate(90f) }
        
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(100, 100, 200, 200), CGSize(100, 100), BitmapFactory.Options(), rotationMatrix, ResizeMode.Cover)

            assertThat(output, notNullValue())
            assertThat(output!!.isRecycled, equalTo(false))
            assertThat(output!!.width, equalTo(100))
            assertThat(output!!.height, equalTo(100))
        }
    }

    @Test
    fun cropAndResize_when_mode_contain_with_matrix_should_work_correctly() {
        val rotationMatrix = Matrix().apply { postRotate(180f) }
        
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(100, 100, 200, 200), CGSize(300, 300), BitmapFactory.Options(), rotationMatrix, ResizeMode.Contain)

            assertThat(output, notNullValue())
            assertThat(output!!.isRecycled, equalTo(false))
            assertThat(output!!.width, equalTo(300))
            assertThat(output!!.height, equalTo(300))
        }
    }

    @Test
    fun cropAndResize_when_mode_stretch_with_matrix_should_work_correctly() {
        val rotationMatrix = Matrix().apply { postRotate(270f) }
        
        FileUtils.openBitmapInputStream(TestHelper.context(), TestHelper.drawableUri(R.drawable.background)).use {
            output = BitmapUtils.cropAndResize(it, CGRect(100, 100, 200, 200), CGSize(150, 250), BitmapFactory.Options(), rotationMatrix, ResizeMode.Stretch)

            assertThat(output, notNullValue())
            assertThat(output!!.isRecycled, equalTo(false))
            assertThat(output!!.width, equalTo(150))
            assertThat(output!!.height, equalTo(250))
        }
    }
}
