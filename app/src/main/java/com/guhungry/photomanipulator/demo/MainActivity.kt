package com.guhungry.photomanipulator.demo

import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import kotlinx.coroutines.*

import com.guhungry.photomanipulator.BitmapUtils
import com.guhungry.photomanipulator.CGRect
import com.guhungry.photomanipulator.CGSize
import com.guhungry.photomanipulator.FileUtils
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //crop()
        //cropAndResize()
        //printText()
        overlayImage()
    }

    private fun crop() {
        GlobalScope.launch {
            try {
                val bee = FileUtils.openBitmapInputStream(applicationContext, "https://images.pexels.com/photos/2017299/pexels-photo-2017299.jpeg?cs=srgb&dl=aerial-shot-beach-bird-s-eye-view-2017299.jpg&fm=jpg?dl&fit=crop&crop=entropy&w=640&h=426")
                val options = BitmapFactory.Options()
                val output = BitmapUtils.crop(bee, CGRect(10, 10, 500, 300), options)
                val file = FileUtils.createTempFile(applicationContext, "CROP_", "image/jpeg")
                Log.d("BEE", "cropFile=$file")
                FileUtils.saveImageFile(output, "image/jpeg", 90, file)
                output.recycle()

            } catch (e: Exception) {
                Log.d("BEE", "errorCrop=$e")
            }
        }
    }

    private fun cropAndResize() {
        GlobalScope.launch {
            try {
                val bee = FileUtils.openBitmapInputStream(
                    applicationContext,
                    "https://images.pexels.com/photos/2017299/pexels-photo-2017299.jpeg?cs=srgb&dl=aerial-shot-beach-bird-s-eye-view-2017299.jpg&fm=jpg?dl&fit=crop&crop=entropy&w=640&h=426"
                )
                val options = BitmapFactory.Options()
                val output = BitmapUtils.cropAndResize(bee, CGRect(10, 10, 500, 250), CGSize(200, 200), options)
                val file = FileUtils.createTempFile(applicationContext, "RESIZE_", "image/jpeg")
                Log.d("BEE", "cropAndResizeFile=$file")
                FileUtils.saveImageFile(output, "image/jpeg", 90, file)
                output.recycle()
            } catch (e: Exception) {
                Log.d("BEE", "errorCropAndResize=$e")
            }
        }
    }

    private fun printText() {
        GlobalScope.launch {
            try {
                val bee = FileUtils.openBitmapInputStream(applicationContext, "https://images.pexels.com/photos/2017299/pexels-photo-2017299.jpeg?cs=srgb&dl=aerial-shot-beach-bird-s-eye-view-2017299.jpg&fm=jpg?dl&fit=crop&crop=entropy&w=640&h=426")
                val options = BitmapFactory.Options().apply {
                    inMutable = true
                }
                val output = BitmapUtils.cropAndResize(bee, CGRect(10, 10, 500, 300), CGSize(500, 300), options)
                val file = FileUtils.createTempFile(applicationContext, "TEXT_", "image/jpeg")

                BitmapUtils.printText(output, PointF(30f, 10f), Color.BLACK, 20f, thickness = 2f)
                BitmapUtils.printText(output, PointF(30f, 10f), Color.WHITE, 20f)
                Log.d("BEE", "textFile=$file")
                FileUtils.saveImageFile(output, "image/jpeg", 90, file)
                output.recycle()

            } catch (e: Exception) {
                Log.d("BEE", "errorText=$e")
            }
        }
    }

    private fun overlayImage() {
        GlobalScope.launch {
            try {
                val bee = FileUtils.openBitmapInputStream(applicationContext, "https://images.pexels.com/photos/2017299/pexels-photo-2017299.jpeg?cs=srgb&dl=aerial-shot-beach-bird-s-eye-view-2017299.jpg&fm=jpg?dl&fit=crop&crop=entropy&w=640&h=426")
                val baby = FileUtils.openBitmapInputStream(applicationContext, "http://www.stickpng.com/assets/images/580b585b2edbce24c47b2951.png")
                val options = BitmapFactory.Options().apply {
                    inMutable = true
                }
                val output = BitmapUtils.cropAndResize(bee, CGRect(10, 10, 500, 300), CGSize(500, 300), options)
                val overlay = BitmapUtils.crop(baby, CGRect(0, 0, 256, 256), options)
                val file = FileUtils.createTempFile(applicationContext, "OVERLAY_", "image/jpeg")

                BitmapUtils.overlay(output, overlay, PointF(50f, 50f))

                Log.d("BEE", "overlayFile=$file")
                FileUtils.saveImageFile(output, "image/jpeg", 90, file)
                output.recycle()
                overlay.recycle()

            } catch (e: Exception) {
                Log.d("BEE", "errorOverlay=$e")
            }
        }
    }
}
