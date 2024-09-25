plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
   // id ("libs.secrets.gradle.plugin")
    //id("kotlin-kapt")

}

android {
    namespace = "com.example.quiztory"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.quiztory"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

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
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation("io.coil-kt:coil-compose:2.4.0")
   // implementation 'com.google.firebase:firebase-auth-ktx:21.1.0'
    //implementation 'com.google.firebase:firebase-firestore-ktx:24.1.0'
    //implementation 'com.google.firebase:firebase-storage-ktx:20.1.0'
    implementation ("com.google.android.gms:play-services-maps:18.0.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation ("com.google.maps.android:maps-compose:2.5.0") // Proverite za najnoviju verziju


}