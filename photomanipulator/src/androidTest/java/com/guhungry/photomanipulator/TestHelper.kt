package com.guhungry.photomanipulator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

object TestHelper {
    fun context(): Context = InstrumentationRegistry.getInstrumentation().targetContext
    private fun packageName() = context().packageName
    fun tempDirectory(): File = context().cacheDir

    fun drawableUri(@DrawableRes res: Int) = "android.resource://${packageName()}/${res}"
    fun drawableBitmap(@DrawableRes res: Int, options: BitmapFactory.Options): Bitmap = BitmapFactory.decodeResource(context().resources, res, options)
}