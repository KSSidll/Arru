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
            val properties = Properties().apply { load(File("signing.properties").reader()) }

            storeFile = File(properties.getProperty("storeFilePath"))
            storePassword = properties.getProperty("storePassword")
            keyPassword = properties.getProperty("keyPassword")
            keyAlias = properties.getProperty("keyAlias")
        }
    }

    namespace = "com.kssidll.arru"
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    ndkVersion = "29.0.13846066 rc3"

    defaultConfig {
        applicationId = "com.kssidll.arru"
        minSdk = 23
        targetSdk = 36
        versionCode = 53
        versionName = "2.7.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    androidResources { generateLocaleConfig = true }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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

    kotlin { jvmToolchain(17) }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeCompiler { includeSourceInformation = true }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

kotlin.sourceSets.main { kotlin.srcDirs(file("${projectDir}/generated/ksp/main/kotlin")) }

dependencies {
    ksp(project(":processor"))

    // Kotlin
    implementation(libs.kotlinx.collections.immutable)

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
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.window.sizeclass)
    implementation(libs.androidx.compose.material.icons.extended)

    // DI
    implementation(libs.google.hilt)
    kapt(libs.google.hilt.compiler)

    // Room
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.paging)

    // Helpers
    implementation(libs.google.accompanist)

    // Paging
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.compose)

    // Text fuzzy search
    implementation(libs.dev.text.search.fuzzywuzzy)

    // Lint
    lintChecks(libs.slack.compose.lint)

    // Chart
    implementation(libs.dev.chart.vico.compose)
    implementation(libs.dev.chart.vico.compose.m3)

    // Testing
    androidTestImplementation(libs.test.android.core)
    androidTestImplementation(libs.test.android.rules)
    androidTestImplementation(libs.test.android.junit)
    androidTestImplementation(libs.test.android.coroutines)
}

ksp { arg(RoomSchemaArgProvider(File(projectDir, "schemas"))) }

class RoomSchemaArgProvider(
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE) val schemaDir: File
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}
