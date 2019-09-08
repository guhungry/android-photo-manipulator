package com.guhungry.photomanipulator
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.test.platform.app.InstrumentationRegistry

object TestHelper {
    fun context() = InstrumentationRegistry.getInstrumentation().targetContext
    fun packageName() = context().packageName
    fun tempDirectory() = context().cacheDir

    fun drawableUri(@DrawableRes res: Int) = "android.resource://${packageName()}/${res}"
    fun drawableBitmap(@DrawableRes res: Int, options: BitmapFactory.Options) = BitmapFactory.decodeResource(context().resources, res, options)
}