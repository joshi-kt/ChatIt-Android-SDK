plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
    id("com.moengage.plugin.maven.publish") version "1.1.0"
}

android {
    namespace = "com.example.chat_it"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.serialization.kotlinx.json)
}