package com.guhungry.photomanipulator

import android.graphics.Point
import android.graphics.Rect
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.AndroidFactoryHelper

data class CGSize(val width: Int, val height: Int) {
    fun ratio() = if (height != 0) width / height.toFloat() else 0f
}

class CGRect(val origin: Point, val size: CGSize, private val factory: AndroidFactory = AndroidFactoryHelper()) {
    @JvmOverloads
    constructor(x: Int, y: Int, width: Int, height: Int, factory: AndroidFactory = AndroidFactoryHelper()) : this(factory.makePoint(x, y), CGSize(width, height), factory)

    fun toRect(): Rect = factory.makeRect(origin.x, origin.y, origin.x + size.width, origin.y + size.height)
}