package com.guhungry.photomanipulator.model

enum class FlipMode(val scaleX: Float, val scaleY: Float) {
    Both(-1f, -1f),
    Horizontal(-1f, 1f),
    None(1f, 1f),
    Vertical(1f, -1f)
}