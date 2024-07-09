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
 * @property shadowRadius Optional shadow radius. Defaults to 0f (no shadow radius).
 * @property shadowOffsetX Optional shadow offset X. Defaults to 0f (no shadow offset x axis).
 * @property shadowOffsetY Optional shadow offset Y. Defaults to 0f (no shadow offset y axis).
 * @property shadowColor Optional shadow color. Defaults to null (no shadow color).
 * @see com.guhungry.photomanipulator.BitmapUtils#printText(Bitmap, String, PointF, TextStyle, AndroidFactory)
 */
data class TextStyle(
    val color: Int,
    val size: Float,
    val font: Typeface? = null,
    val alignment: Paint.Align = Paint.Align.LEFT,
    val thickness: Float = 0f,
    val rotation: Float? = null,
    val shadowRadius: Float = 0f,
    val shadowOffsetX: Float = 0f,
    val shadowOffsetY: Float = 0f,
    val shadowColor: Int? = null
)
