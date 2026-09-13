plugins {
    id("com.android.application") version "9.4.0" apply false
    id("org.jetbrains.kotlin.android") version "2.3.21" apply false
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