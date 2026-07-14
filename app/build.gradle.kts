plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.gcap"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gcap"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "ANALYTICS_BASE_URL",
            "\"https://gcap-admin.vercel.app/\"",
        )
        buildConfigField("String", "ANALYTICS_API_KEY", "\"gcap-analytics-8f3k2m9x7p1q4w6z\"")
        buildConfigField("boolean", "ANALYTICS_ENABLED", "true")
        // OneSignal App ID from dashboard Keys & IDs (not the REST API key)
        buildConfigField("String", "ONESIGNAL_APP_ID", "\"3acc100c-877e-49d4-9a51-d65fa4e77c86\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "ANALYTICS_BASE_URL",
                "\"https://gcap-admin.vercel.app/\"",
            )
        }
        release {
            isMinifyEnabled = false
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.media3.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.github.zcweng:switch-button:0.0.3")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // Pin exact 5.1.x — dynamic 5.6+ ranges crash Kotlin 2.0.21 (FirIncompatibleClass / source null)
    implementation("com.onesignal:OneSignal:5.1.26")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    implementation("io.github.walterinkitchen:mini-xlsx-reader:1.0.5")
}

// Apply only when Firebase config is present (required for OneSignal/FCM on Android).
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}
