plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
    alias(libs.plugins.secrets.gradle.plugin)
}

android {
    namespace = "com.example.teamproject8"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.teamproject8"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "NAVER_CLIENT_ID",
            "\"${project.findProperty("NAVER_CLIENT_ID")}\""
        )
        buildConfigField(
            "String",
            "NAVER_CLIENT_SECRET",
            "\"${project.findProperty("NAVER_CLIENT_SECRET")}\""
        )
        buildConfigField(
            "String",
            "NAVER_SEARCH_CLIENT_ID",
            "\"${project.findProperty("NAVER_SEARCH_CLIENT_ID")}\""
        )
        buildConfigField(
            "String",
            "NAVER_SEARCH_CLIENT_SECRET",
            "\"${project.findProperty("NAVER_SEARCH_CLIENT_SECRET")}\""
        )
        buildConfigField(
            "String",
            "GOOGLE_API_KEY",
            "\"${project.findProperty("GOOGLE_API_KEY")}\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.navigation.common.android)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation("com.naver.maps:map-sdk:3.21.0") // Naver Map SDK
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit for API
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1") // Gson
    implementation(libs.naver.map.compose)
    implementation(libs.accompanist.permissions)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    annotationProcessor(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    val room_version = "2.7.2" // 최신 버전으로 확인하세요

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // Kotlin 확장 및 Coroutines 지원 (권장)
    ksp("androidx.room:room-compiler:$room_version") // KSP용 Room
    implementation("androidx.room:room-ktx:$room_version")
    implementation("com.google.code.gson:gson:2.10.1")
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}