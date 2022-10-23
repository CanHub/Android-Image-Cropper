package com.canhub.cropper

import android.graphics.BitmapFactory
import android.widget.ImageView
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test
import java.io.File

class CropImageViewTest {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = PIXEL_5,
    theme = "Theme.MaterialComponents.DayNight.DarkActionBar",
  )

  @Test fun ovalBitmap() {
    val file = fileFromAsset("small-tree.jpg")
    val imageView = ImageView(paparazzi.context)
    imageView.setImageBitmap(CropImage.toOvalBitmap(BitmapFactory.decodeStream(file.inputStream())))
    paparazzi.snapshot(imageView)
  }

  private fun fileFromAsset(name: String) =
    File(CropImageViewTest::class.java.classLoader?.getResource(name)?.file!!)
}
