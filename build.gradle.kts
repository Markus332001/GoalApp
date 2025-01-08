


// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("androidx.navigation.safeargs") version "2.8.5" apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

extra.apply {
    set("room_version", "2.6.0")
}