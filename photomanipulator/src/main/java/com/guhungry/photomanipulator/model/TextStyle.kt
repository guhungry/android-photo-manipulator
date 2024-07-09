package com.guhungry.photomanipulator.model

import android.graphics.Paint
import android.graphics.Typeface

/**
 * Defines the style for text drawn on a Bitmap.
 *
 * @property color Color of the text.
 * @property size Size of the text.
 * @property font Optional font for the text. Defaults to null (system default).
 * @property alignment Optional text alignment. Defaults to[Paint.Align.LEFT].
 * @property thickness Optional border thickness. Defaults to 0f (no border).
 * @property rotation Optional rotation of the text in degrees. Defaults to null (no rotation).
 * @see com.guhungry.photomanipulator.BitmapUtils#printText(Bitmap, String, PointF, TextStyle, AndroidFactory)
 */
data class TextStyle(
    val color: Int,
    val size: Float,
    val font: Typeface? = null,
    val alignment: Paint.Align = Paint.Align.LEFT,
    val thickness: Float = 0f,
    val rotation: Float? = null
)
