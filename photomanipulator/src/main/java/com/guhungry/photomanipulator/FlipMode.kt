package com.guhungry.photomanipulator

enum class FlipMode(val scaleX: Float, val scaleY: Float) {
    Both(-1f, -1f),
    Horizontal(-1f, 1f),
    None(0f, 0f),
    Vertical(1f, -1f)
}