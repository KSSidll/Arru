import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
}

android {
    signingConfigs {
        create("devel") {
            val properties = Properties().apply {
                load(File("signing.properties").reader())
            }

            storeFile = File(properties.getProperty("storeFilePath"))
            storePassword = properties.getProperty("storePassword")
            keyPassword = properties.getProperty("keyPassword")
            keyAlias = properties.getProperty("keyAlias")
        }
    }
    namespace = "com.kssidll.arru"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kssidll.arru"
        minSdk = 21
        targetSdk = 35
        versionCode = 36
        versionName = "2.4.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("devel")
        }

        debug {
            signingConfig = signingConfigs.getByName("devel")
            isJniDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeCompiler {
        enableStrongSkippingMode = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    // AndroidX
    implementation(libs.androidx.activity)
    implementation(libs.androidx.splashscreen.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.datastore.preferences)

    // Navigation
    implementation(libs.dev.navigation.reimagined)
    implementation(libs.dev.navigation.reimagined.hilt)

    // Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.window.sizeclass)
    implementation(libs.androidx.compose.material.icons.extended)

    // DI
    implementation(libs.google.hilt)
    kapt(libs.google.hilt.compiler)

    // Room
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Paging
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.compose)

    // Text fuzzy search
    implementation(libs.dev.text.search.fuzzywuzzy)

    // Chart
    implementation(libs.dev.chart.vico.compose)
    implementation(libs.dev.chart.vico.compose.m3)
}

ksp {
    arg(
        RoomSchemaArgProvider(
            File(
                projectDir,
                "schemas"
            )
        )
    )
}

class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File
): CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}
