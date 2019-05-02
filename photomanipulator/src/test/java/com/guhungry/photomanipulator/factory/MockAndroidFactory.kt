package com.guhungry.photomanipulator.factory

import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import org.mockito.Mockito
import org.mockito.Mockito.mock

class MockAndroidFactory: AndroidFactory {
    override fun makePoint(x: Int, y: Int): Point {
        return Mockito.mock(Point::class.java).apply {
            this.x = x
            this.y = y
        }
    }

    override fun makeRect(left: Int, top: Int, right: Int, bottom: Int): Rect {
        return mock(Rect::class.java).apply {
            this.left = left
            this.top = top
            this.right = right
            this.bottom = bottom
        }
    }
    override fun makeUri(uri: String): Uri = mock(Uri::class.java)
}