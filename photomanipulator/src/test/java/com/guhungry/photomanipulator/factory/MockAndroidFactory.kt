package com.guhungry.photomanipulator.factory

import android.graphics.*
import android.net.Uri
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.InputStream

class MockAndroidFactory: AndroidFactory {
    override fun makePoint(x: Int, y: Int): Point = mock(Point::class.java).apply {
        this.x = x
        this.y = y
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
    override fun makeCanvas(image: Bitmap): Canvas {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun makePaint(): Paint {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fetchUrl(uri: String): InputStream = ByteArrayInputStream(ByteArray(0))
}