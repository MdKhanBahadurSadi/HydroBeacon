plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Firebase plugin
}

android {
    namespace = "com.sadi.hydrobeacon"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sadi.hydrobeacon"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
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

    buildFeatures {
        viewBinding = true
    }

    packaging {
        jniLibs {
            pickFirsts += "**/libtensorflowlite_jni.so"
        }
    }

    lint {
        abortOnError = false
        htmlReport = true
        xmlReport = false
        lintConfig = file("src/main/res/xml/lint.xml")
    }
}

dependencies {
    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

    // Firebase products
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-analytics")

    // AI & Gemini
    implementation(libs.generativeai)

    // TensorFlow Lite for Prediction
    // We remove explicit tensorflow-lite as it conflicts with LiteRT brought in by generativeai
    // implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)

    // Networking for Weather API
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // CameraX for Image detection
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.8.0")
    implementation("androidx.navigation:navigation-ui:2.8.0")

    // MPAndroidChart for visualization
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Other default dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.test.rules)
}
