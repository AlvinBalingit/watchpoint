import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.watchpoint.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.watchpoint.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 13
        versionName = "watchpoint.13"
        vectorDrawables { useSupportLibrary = true }
    }

    signingConfigs {
        create("release") {
            val keystoreProps = Properties()
            val keystorePropsFile = rootProject.file("keystore.properties")
            if (keystorePropsFile.exists()) {
                keystoreProps.load(FileInputStream(keystorePropsFile))
            }
            fun prop(key: String) =
                (keystoreProps.getProperty(key) ?: System.getenv(key))
                    ?: error("Missing $key: add it to keystore.properties (gitignored) or set env var $key")

            storeFile = file(prop("WATCHPOINT_STORE_FILE"))
            storePassword = prop("WATCHPOINT_STORE_PASSWORD")
            keyAlias = prop("WATCHPOINT_KEY_ALIAS")
            keyPassword = prop("WATCHPOINT_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)
}
