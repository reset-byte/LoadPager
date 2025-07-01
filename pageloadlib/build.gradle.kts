plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")

    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.github.pageloadlib"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    api(libs.refresh.layout.kernel)
    api(libs.multitype)
    api(libs.shimmerlayout)
    api(libs.lottie)
    api( libs.androidx.lifecycle.viewmodel.ktx.v287)
    api( libs.androidx.lifecycle.livedata.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.refresh.header.classics)
    
    // Network dependencies
    api(libs.retrofit)
    api(libs.retrofit.gson)
    api(libs.okhttp)
    api(libs.okhttp.logging)
    api(libs.gson)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)
}