package com.guhungry.photomanipulator

import android.graphics.Matrix
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.MockAndroidFactory
import com.guhungry.photomanipulator.model.CGRect
import com.guhungry.photomanipulator.model.CGSize
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class BitmapUtilsResizeTest {
    private lateinit var factory: AndroidFactory
    private lateinit var matrix: Matrix

    @Before
    fun setUp() {
        val mockFactory = MockAndroidFactory()
        factory = spy(mockFactory)
        matrix = mock(Matrix::class.java)
        doReturn(matrix).`when`(factory).makeMatrix()
    }

    @Test
    fun `findCropPositionStretchOrContain should return correct rect scaled by sample size`() {
        val rect = CGRect(100, 200, 300, 400, factory)
        val sampleSize = 2

        val actual = BitmapUtils.findCropPositionStretchOrContain(rect, sampleSize, factory)

        assertThat(actual.origin.x, equalTo(50))
        assertThat(actual.origin.y, equalTo(100))
        assertThat(actual.size.width, equalTo(150))
        assertThat(actual.size.height, equalTo(200))
    }

    @Test
    fun `findCropScaleCover when landscape to portrait should crop sides`() {
        // Source: 100x50 (2:1)
        // Target: 50x50 (1:1)
        // Should scale by height: 50/50 = 1.0
        val rect = CGRect(0, 0, 100, 50, factory)
        val target = CGSize(50, 50)
        
        BitmapUtils.findCropScaleCover(rect, target, factory)
        
        verify(matrix).setScale(1.0f, 1.0f)
    }

    @Test
    fun `findCropScaleCover when portrait to landscape should crop top bottom`() {
        // Source: 50x100 (1:2)
        // Target: 50x50 (1:1)
        // Should scale by width: 50/50 = 1.0
        val rect = CGRect(0, 0, 50, 100, factory)
        val target = CGSize(50, 50)

        BitmapUtils.findCropScaleCover(rect, target, factory)

        verify(matrix).setScale(1.0f, 1.0f)
    }
    
    @Test
    fun `findCropScaleCover when upscale should match target`() {
        // Source: 10x10
        // Target: 100x100
        // Scale: 10
        val rect = CGRect(0, 0, 10, 10, factory)
        val target = CGSize(100, 100)

        BitmapUtils.findCropScaleCover(rect, target, factory)

        verify(matrix).setScale(10.0f, 10.0f)
    }

    @Test
    fun `findCropScaleContain should scale to fit`() {
        // Source: 100x100
        // Target: 50x25
        // Scale X: 0.5, Scale Y: 0.25
        // Contain should use smaller: 0.25
        val rect = CGRect(0, 0, 100, 100, factory)
        val target = CGSize(50, 25)

        BitmapUtils.findCropScaleContain(rect, target, factory)

        verify(matrix).setScale(0.25f, 0.25f)
    }

    @Test
    fun `findCropScaleStretch should scale independently`() {
        // Source: 100x100
        // Target: 50x25
        // Scale X: 0.5, Scale Y: 0.25
        val rect = CGRect(0, 0, 100, 100, factory)
        val target = CGSize(50, 25)

        BitmapUtils.findCropScaleStretch(rect, target, factory)

        verify(matrix).setScale(0.5f, 0.25f)
    }
}
