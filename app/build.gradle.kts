plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
}

android {
    namespace = "com.example.gbtranslate"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gbtranslate"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("com.airbnb.android:lottie-compose:6.7.1")

    implementation(libs.androidx.room.runtime)
    kapt("androidx.room:room-compiler:2.7.2")
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.material3)
    annotationProcessor(libs.androidx.room.compiler)
    implementation("androidx.room:room-ktx:2.7.2")
    //noinspection UseTomlInstead

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.navigation3.ui.android)

    implementation("com.google.mlkit:translate:17.0.3")

    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")
// To recognize Chinese script
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-chinese:16.0.1")
// To recognize Devanagari script
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-devanagari:16.0.1")
// To recognize Japanese script
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-japanese:16.0.1")
// To recognize Korean script
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-korean:16.0.1")
// Test helpers
    testImplementation("androidx.room:room-testing:2.7.2")
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.coil.compose)
    val camerax_version = "1.6.0-alpha01"

    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
// If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
// If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${camerax_version}")
// If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${camerax_version}")
// If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${camerax_version}")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}