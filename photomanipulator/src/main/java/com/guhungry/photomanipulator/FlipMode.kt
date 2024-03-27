package com.guhungry.photomanipulator

enum class FlipMode(val scaleX: Float, val scaleY: Float) {
    Horizontal(-1f, 1f),
    Vertical(1f, -1f)
}