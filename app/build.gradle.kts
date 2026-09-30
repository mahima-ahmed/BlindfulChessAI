plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.blindfulchessai"
    compileSdk {
        version = release(37)
    }
    // Esta es la nueva configuración moderna para que no se comprima la IA:
    androidResources {
        noCompress += "tflite"
    }


    defaultConfig {
        applicationId = "com.example.blindfulchessai"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // Versión moderna que soluciona la colisión de Namespace en Gradle 9+
    implementation("org.tensorflow:tensorflow-lite:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

}