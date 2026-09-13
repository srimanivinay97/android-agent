plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.vinay.androidagent"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vinay.androidagent"

        // Redmi 9i is old enough that we keep this low.
        minSdk = 23
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
}