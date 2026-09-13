plugins {
    id("com.android.application")
}

android {
    namespace = "com.vinay.androidagent"
    compileSdk = 36

    defaultConfig {
    applicationId = "com.vinay.androidagent"
    minSdk = 23
    targetSdk = 36

    val buildNumber =
        System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1

    versionCode = buildNumber
    versionName = "1.0.$buildNumber"

    val repo =
    System.getenv("GITHUB_REPOSITORY")
        ?: "YOUR_GITHUB_USERNAME/android-agent"

resValue("string", "github_repository", repo)
}

    signingConfigs {
        create("release") {
            storeFile = file("android-agent-release.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEYSTORE_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
}