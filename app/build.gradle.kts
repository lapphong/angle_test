import com.android.build.api.variant.impl.VariantOutputImpl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

fun getDate(): String {
    val date = Date()
    val sdf = SimpleDateFormat("yyyyMMdd")
    return sdf.format(date)
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

apply(from = "signingConfigs.gradle")

android {
    namespace = "com.example.face_lens"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.face_lens"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            optimization {
                enable = false
            }
        }
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
    lint {
        abortOnError = true
        checkReleaseBuilds = true
    }

    //noinspection WrongGradleMethod
    androidComponents {
        onVariants { variant ->
            variant.outputs
                .forEach { output ->
                    val variantOutput = output as VariantOutputImpl

                    val applicationId = variant.applicationId.get() // com.exampleFree.app
                    val versionName = variantOutput.versionName.get() // e.g 1.0.0
                    val versionCode = variantOutput.versionCode.get() // e.g 1
                    val flavorName = variant.flavorName ?: "default" // e.g. Free
                    val buildType = variant.buildType // e.g. debug
                    val variantName = variant.name // e.g. FreeDebug

                    //customize your app name by using variables
                    variantOutput.outputFileName = "${flavorName}_v${versionName}_${buildType}_${getDate()}.apk"
                }
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.mlkit.vision)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.mlkit.face.detection)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
