package com.guhungry.photomanipulator.factory

import android.graphics.Point
import android.graphics.Rect
import android.net.Uri

interface AndroidFactory {
    fun makePoint(x: Int, y: Int): Point
    fun makeRect(left: Int, top: Int, right: Int, bottom: Int): Rect
    fun makeUri(uri: String): Uri
}

class AndroidFactoryHelper: AndroidFactory {
    override fun makePoint(x: Int, y: Int): Point = Point(x, y)
    override fun makeRect(left: Int, top: Int, right: Int, bottom: Int): Rect = Rect(left, top, right, bottom)
    override fun makeUri(uri: String): Uri = Uri.parse(uri)
}