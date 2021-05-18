package com.guhungry.photomanipulator.demo

import android.app.Application
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guhungry.photomanipulator.*
import com.guhungry.photomanipulator.demo.item.ExampleItem
import com.guhungry.photomanipulator.demo.item.ListItem
import com.guhungry.photomanipulator.demo.item.SectionItem
import java.io.File
import java.util.concurrent.Executors

class ExampleViewModel : ViewModel() {
    lateinit var application: Application

    private val items: MutableLiveData<List<ListItem>> by lazy {
        MutableLiveData<List<ListItem>>().also {
            asyncLoadExamples()
        }
    }

    fun getExamples(): LiveData<List<ListItem>> {
        return items
    }

    private fun asyncLoadExamples() {
        Executors.newFixedThreadPool(1)
            .execute {
                clearCache()
                loadExamples()
        }
    }

    private fun clearCache() {
        FileUtils.cleanDirectory(FileUtils.cachePath(application)!!, "apm_")
    }

    private fun loadExamples() {
        items.postValue(listOf(
            SectionItem("Background Image"),
            ExampleItem("", "") Supplier@{
                return@Supplier getBitmap(R.drawable.background)
            },
            SectionItem("Overlay Image"),
            ExampleItem("", "") Supplier@{
                return@Supplier getBitmap(R.drawable.overlay)
            },
            SectionItem("Overlay"),
            ExampleItem(
                "Overlay",
                "BitmapUtils.overlay(background, overlay, PointF(750f, 500f))"
            ) Supplier@{
                val background = getBitmap(R.drawable.background, optionsMutable())
                val overlay = getBitmap(R.drawable.overlay)
                BitmapUtils.overlay(background, overlay, PointF(750f, 500f))
                overlay.recycle()

                return@Supplier background
            },
            SectionItem("Print Text"),
            ExampleItem(
                "Overlay",
                "BitmapUtils.printText(background, text, location, color, textSize, font)"
            ) Supplier@{
                val background = getBitmap(R.drawable.background, optionsMutable())
                BitmapUtils.printText(
                    background,
                    "Test Text Value",
                    PointF(750f, 500f),
                    Color.GREEN,
                    200f,
                    getFont(R.font.girassol_regular)
                )

                return@Supplier background
            },
            SectionItem("Crop"),
            ExampleItem("Crop", "BitmapUtils.crop(image, region, bitmapOptions)") Supplier@{
                readInputStream(R.drawable.overlay).use {
                    return@Supplier BitmapUtils.crop(
                        it,
                        CGRect(20, 10, 100, 300),
                        optionsMutable()
                    )
                }
            },
            SectionItem("Crop & Resize"),
            ExampleItem(
                "Crop & Resize",
                "BitmapUtils.cropAndResize(inputStream, region, targetSize, bitmapOptions)"
            ) Supplier@{
                readInputStream(R.drawable.background).use {
                    return@Supplier BitmapUtils.cropAndResize(
                        it,
                        CGRect(100, 80, 300, 700),
                        CGSize(200, 100),
                        optionsMutable()
                    )
                }
            },
            SectionItem("Mixed"),
            ExampleItem(
                "Overlay & Print Text",
                ""
            ) Supplier@{
                val background = getBitmap(R.drawable.background, optionsMutable())
                val overlay = getBitmap(R.drawable.overlay)
                BitmapUtils.overlay(background, overlay, PointF(500f, 500f))


                val text = "Test Text Value"
                val font = getFont(R.font.girassol_regular)
                BitmapUtils.printText(background, text, PointF(750f, 500f), Color.WHITE, 200f, font, Paint.Align.LEFT, 10f)
                BitmapUtils.printText(background, text, PointF(750f, 500f), Color.GREEN, 200f, font)

                BitmapUtils.overlay(background, overlay, PointF(1000f, 300f))

                BitmapUtils.printText(background, text, PointF(250f, 300f), Color.YELLOW, 200f, null, Paint.Align.LEFT, 20f)
                BitmapUtils.printText(background, text, PointF(250f, 300f), Color.MAGENTA, 200f)

                BitmapUtils.overlay(background, overlay, PointF(1500f, 100f))
                overlay.recycle()

                return@Supplier background
            },
            SectionItem("Optimize"),
            ExampleItem(
                "5%",
                ""
            ) Supplier@{
                val background = getBitmap(R.drawable.background, optionsMutable())
                val file = getTempFile()
                FileUtils.saveImageFile(background, MimeUtils.JPEG, 5, file)
                background.recycle()

                return@Supplier BitmapFactory.decodeFile(file.path)
            },
            ExampleItem(
                "10%",
                ""
            ) Supplier@{
                val background = getBitmap(R.drawable.background, optionsMutable())
                val file = getTempFile()
                FileUtils.saveImageFile(background, MimeUtils.JPEG, 10, file)
                background.recycle()

                return@Supplier BitmapFactory.decodeFile(file.path)
            },
            ExampleItem(
                "15%",
                ""
            ) Supplier@{
                val background = getBitmap(R.drawable.background, optionsMutable())
                val file = getTempFile()
                FileUtils.saveImageFile(background, MimeUtils.JPEG, 15, file)
                background.recycle()

                return@Supplier BitmapFactory.decodeFile(file.path)
            },
            ExampleItem(
                "100%",
                ""
            ) Supplier@{
                val background = getBitmap(R.drawable.background, optionsMutable())
                val file = getTempFile()
                FileUtils.saveImageFile(background, MimeUtils.JPEG, 100, file)
                background.recycle()

                return@Supplier BitmapFactory.decodeFile(file.path)
            }
        ))
    }

    private fun getTempFile() =
        FileUtils.createTempFile(application, "apm_", MimeUtils.JPEG);

    private fun optionsMutable(): BitmapFactory.Options = BitmapFactory.Options().apply {
        inMutable = true
    }

    private fun readInputStream(resource: Int) =
        application.resources.openRawResource(resource)

    private fun getBitmap(resource: Int, options: BitmapFactory.Options? = null) =
        BitmapFactory.decodeResource(application.resources, resource, options)

    private fun getFont(resource: Int) =
        ResourcesCompat.getFont(application, resource)
}