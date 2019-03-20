package com.guhungry.photomanipulator

import android.graphics.Point
import android.graphics.Rect

class CGSize(val width: Int, val height: Int) {
    fun ratio() = if (height != 0) width / height.toFloat() else 0f
}

class CGRect(val origin: Point, val size: CGSize) {
    constructor(x: Int, y: Int, width: Int, height: Int) : this(Point(x, y), CGSize(width, height))

    fun toRect(): Rect = Rect(origin.x, origin.y, origin.x + size.width, origin.y + size.height)
}