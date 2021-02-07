package com.canhub.cropper.sample

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.sample.main.app.MainFragment
import com.example.croppersample.R

internal class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commit()
    }
}