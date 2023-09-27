plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.daggerHiltAndroid)
    kotlin("kapt")
    id("kotlin-parcelize")
    alias(libs.plugins.devtoolsKSP)
}

android {
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

        //        resourceConfigurations += ["en", "pl"]
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            //            signingConfig = signingConfigs.
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
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // AndroidX
    //    implementation("androidx.activity:activity-compose:1.7.2")
    //    implementation("androidx.core:core-splashscreen:1.0.1")
    //    implementation("androidx.appcompat:appcompat:1.7.0-alpha03")
    //    implementation("androidx.datastore:datastore-preferences-android:1.1.0-alpha05")
    //    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    //    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.appcompat)
    implementation(libs.datastore.preferences)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.accompanist.systemuicontroller)

    // Navigation
    //    implementation("dev.olshevski.navigation:reimagined:1.5.0")
    //    implementation("dev.olshevski.navigation:reimagined-hilt:1.5.0")
    implementation(libs.reimagined)
    implementation(libs.reimagined.hilt)

    // Compose
    //    implementation("androidx.compose.ui:ui:1.6.0-alpha06")
    //    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0-alpha06")
    //    debugImplementation "androidx.compose.ui:ui-tooling:1.6.0-alpha06"
    //    debugImplementation "androidx.compose.ui:ui-test-manifest:1.6.0-alpha06"
    //    implementation("androidx.compose.material3:material3:1.2.0-alpha08")
    //    implementation("androidx.compose.material3:material3-window-size-class:1.2.0-alpha08")
    //    implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha06")
    //    implementation("androidx.compose.material:material:1.6.0-alpha06")
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(libs.material3)
    implementation(libs.material3.window.sizeclass)
    implementation(libs.material.icons.extended)
    implementation(libs.material)

    // DI
    //    implementation("com.google.dagger:hilt-android:2.48")
    //    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)

    // Room
    //    implementation("androidx.room:room-ktx:2.6.0-rc01")
    //    ksp("androidx.room:room-compiler:2.6.0-rc01")
    //    ksp("androidx.room:room-ktx:2.6.0-rc01")
    implementation(libs.room.ktx)
    ksp(libs.room.ktx)
    ksp(libs.room.compiler)


    //    implementation("me.xdrop:fuzzywuzzy:1.4.0")
    //    implementation("com.patrykandpatrick.vico:compose:1.12.0")
    //    implementation("com.patrykandpatrick.vico:compose-m3:1.12.0")
    implementation(libs.fuzzywuzzy)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
}

ksp {
    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
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
