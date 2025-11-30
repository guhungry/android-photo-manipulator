package com.guhungry.photomanipulator.factory

import android.graphics.*
import android.net.Uri
import androidx.core.net.toUri
import java.io.InputStream
import java.net.URL

interface AndroidFactory {
    fun makePoint(x: Int, y: Int): Point
    fun makeRect(left: Int, top: Int, right: Int, bottom: Int): Rect
    fun makeUri(uri: String): Uri
    fun makeCanvas(image: Bitmap): Canvas
    fun makePaint(): Paint
    fun fetchUrl(uri: String): InputStream
}

class AndroidConcreteFactory: AndroidFactory {
    override fun makePoint(x: Int, y: Int): Point = Point(x, y)
    override fun makeRect(left: Int, top: Int, right: Int, bottom: Int): Rect = Rect(left, top, right, bottom)
    override fun makeUri(uri: String): Uri = uri.toUri()
    override fun makeCanvas(image: Bitmap): Canvas = Canvas(image)
    override fun makePaint(): Paint = Paint()
    override fun fetchUrl(uri: String): InputStream = URL(uri).openConnection().getInputStream()
}