package com.guhungry.imageeditor

import android.graphics.Point

class CGSize(val width: Int, val height: Int) {
    fun ratio() = if (height != 0) width / height.toFloat() else 0f
}

class CGRect(val origin: Point, val size: CGSize) {
    constructor(x: Int, y: Int, width: Int, height: Int) : this(Point(x, y), CGSize(width, height))
}