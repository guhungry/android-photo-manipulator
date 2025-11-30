package com.guhungry.photomanipulator.demo

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.guhungry.photomanipulator.BitmapUtils
import com.guhungry.photomanipulator.demo.ui.theme.MyApplicationTheme
import com.guhungry.photomanipulator.model.TextStyle


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        println("BEE")
        val bee = ImageUtils.bitmapFromUri(
            this.applicationContext, "https://github.com/guhungry/react-native-photo-manipulator/blob/34c982bbba3837b32de230d2aad30aae18cf65fd/docs/demo-background.jpg?raw=true", ImageUtils.mutableOptions()
        )
        val style = TextStyle(Color.BLACK, 40f, rotation = -45f)
        BitmapUtils.printText(bee, "BEE\nhaha", PointF(50f, 50f), style)
        println(bee)
        println(bee.height)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}