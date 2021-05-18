package com.guhungry.photomanipulator.demo.item

import android.graphics.Bitmap
import java9.util.function.Supplier

class ExampleItem(override val title: String, val kotlin: String, supplier: Supplier<Bitmap>): ListItem {
    override val type: Int = 1
    val image: Bitmap = supplier.get()
}