package com.guhungry.photomanipulator.factory

import android.graphics.PointF
import org.mockito.Mockito.mock

object TestHelpers {
    internal fun mockPointF(x: Float, y: Float): PointF {
        return mock(PointF::class.java).apply {
            this.x = x
            this.y = y
        }
    }
}