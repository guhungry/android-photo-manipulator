package com.guhungry.photomanipulator.model

enum class ResizeMode {
    Cover,    // Scale to fill, crop excess (maintains aspect ratio)
    Contain,  // Scale to fit, maintain aspect ratio (may have empty space)
    Stretch   // Scale to exact size (ignores aspect ratio, may distort)
}
