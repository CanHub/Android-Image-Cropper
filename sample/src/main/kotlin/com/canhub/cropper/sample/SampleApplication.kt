package com.canhub.cropper.sample

import android.app.Application
import timber.log.Timber

class SampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())
  }
}
