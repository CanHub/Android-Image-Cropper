buildscript {
  dependencies {
    classpath(libs.plugin.android.cache.fix)
    classpath(libs.plugin.androidgradleplugin)
    classpath(libs.plugin.dokka)
    classpath(libs.plugin.kotlin)
    classpath(libs.plugin.licensee)
    classpath(libs.plugin.paparazzi)
    classpath(libs.plugin.publish)
  }
}

plugins {
  alias(libs.plugins.codequalitytools)
}

codeQualityTools {
  checkstyle {
    enabled = false // Kotlin only.
  }
  pmd {
    enabled = false // Kotlin only.
  }
  ktlint {
    toolVersion = libs.versions.ktlint.get()
    experimental = true
  }
  detekt {
    enabled = false // Don"t want.
  }
  cpd {
    enabled = false // Kotlin only.
  }
  lint {
    lintConfig = rootProject.file("lint.xml")
    checkAllWarnings = true
  }
  kotlin {
    allWarningsAsErrors = true
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

subprojects {
  plugins.withType<com.android.build.gradle.api.AndroidBasePlugin> {
    apply(plugin = "org.gradle.android.cache-fix")
  }

  tasks.withType(Test::class.java).all {
    testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
  }
}
