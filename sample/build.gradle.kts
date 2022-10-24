plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
  namespace = "com.example.croppersample"

  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "com.example.croppersample"
    vectorDrawables.useSupportLibrary = true
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = 1
    versionName = project.property("VERSION_NAME").toString()
  }

  buildFeatures {
    viewBinding = true
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      isShrinkResources = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }
}

dependencies {
  implementation(project(":cropper"))
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core.ktx)
  implementation(libs.material)
  implementation(libs.timber)
}

dependencies {
  debugImplementation(libs.leakcanary.android)
}
