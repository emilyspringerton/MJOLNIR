plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    kotlin("kapt")
}

android {
    namespace = "industrial.einhorn.mjolnir"
    compileSdk = 34

    defaultConfig {
        applicationId = "industrial.einhorn.mjolnir"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        debug {
            buildConfigField("String", "IDUNA_BASE_URL", "\"http://10.0.2.2:8090\"")
            buildConfigField("String", "EMILY_BASE_URL", "\"http://10.0.2.2:8086\"")
            buildConfigField("String", "FATBABY_BASE_URL", "\"http://10.0.2.2:8082\"")
            buildConfigField("String", "MJOLNIR_AGENT_NAME", "\"mjolnir-emily\"")
        }
        create("staging") {
            initWith(getByName("debug"))
            buildConfigField("String", "IDUNA_BASE_URL", "\"https://iduna.farthq.com\"")
            buildConfigField("String", "EMILY_BASE_URL", "\"https://iduna.farthq.com\"")
            buildConfigField("String", "FATBABY_BASE_URL", "\"https://iduna.farthq.com\"")
            buildConfigField("String", "MJOLNIR_AGENT_NAME", "\"mjolnir-emily\"")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "IDUNA_BASE_URL", "\"https://iduna.einhorn.industrial\"")
            buildConfigField("String", "EMILY_BASE_URL", "\"https://emily.einhorn.industrial\"")
            buildConfigField("String", "FATBABY_BASE_URL", "\"https://fatbaby.einhorn.industrial\"")
            buildConfigField("String", "MJOLNIR_AGENT_NAME", "\"mjolnir-emily\"")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)

    // Hilt DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Firebase (FCM)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.google.auth.play)

    // Security
    implementation(libs.encrypted.prefs)

    // Background sync
    implementation(libs.work.runtime)

    // APPLES offline git sync
    implementation(libs.jgit)

    // WebView
    implementation(libs.accompanist.webview)

    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // Image loading (observation result thumbnails)
    implementation(libs.coil.compose)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    testImplementation(libs.junit)
}
