// path: androidApp/build.gradle.kts
plugins {
    alias(libs.plugins.androidApplication)
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

android {
    namespace = "com.chris.mobilewallet.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.chris.mobilewallet"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
}
