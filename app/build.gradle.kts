import java.util.*

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.daggerHiltAndroid)
    kotlin("kapt")
    id("kotlin-parcelize")
    alias(libs.plugins.devtoolsKSP)
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
    namespace = "com.kssidll.arrugarq"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kssidll.arrugarq"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        resourceConfigurations.addAll(
            listOf(
                "en",
                "pl",
            )
        )
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
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

dependencies {
    // AndroidX
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.appcompat)
    implementation(libs.datastore.preferences)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.accompanist.systemuicontroller)

    // Navigation
    implementation(libs.reimagined)
    implementation(libs.reimagined.hilt)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(libs.material3)
    implementation(libs.material3.window.sizeclass)
    implementation(libs.material.icons.extended)

    // DI
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.room.ktx)
    ksp(libs.room.ktx)
    ksp(libs.room.compiler)

    // Other
    implementation(libs.fuzzywuzzy)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
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
