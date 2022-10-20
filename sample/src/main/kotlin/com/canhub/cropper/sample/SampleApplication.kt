package com.canhub.cropper.sample

import android.app.Application
import android.os.StrictMode
import timber.log.Timber

class SampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())

    StrictMode.setThreadPolicy(
      StrictMode.ThreadPolicy.Builder().detectAll()
        .penaltyLog()
        .penaltyFlashScreen()
        .build(),
    )

    StrictMode.setVmPolicy(
      StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyDeath()
        .penaltyLog()
        .build(),
    )
  }
}
