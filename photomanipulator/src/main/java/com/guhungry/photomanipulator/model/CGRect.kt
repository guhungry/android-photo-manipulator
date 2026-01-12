package com.guhungry.photomanipulator.model

import android.graphics.Point
import android.graphics.Rect
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.AndroidConcreteFactory

data class CGSize(val width: Int, val height: Int) {
    /**
     * Calculate aspect ratio (width / height).
     *
     * @return Aspect ratio as float, or 0f if height is 0 (to avoid division by zero)
     */
    fun ratio() = if (height != 0) width / height.toFloat() else 0f
}

class CGRect(val origin: Point, val size: CGSize, private val factory: AndroidFactory = AndroidConcreteFactory()) {
    @JvmOverloads
    constructor(x: Int, y: Int, width: Int, height: Int, factory: AndroidFactory = AndroidConcreteFactory()) : this(factory.makePoint(x, y), CGSize(width, height), factory)

    fun toRect(): Rect = factory.makeRect(origin.x, origin.y, origin.x + size.width, origin.y + size.height)
}