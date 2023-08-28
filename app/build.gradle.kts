plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.kotlin)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.appdistribution)
}

android {

    val keystoreFilePath: String by rootProject.extra
    val keystorePassword: String by rootProject.extra
    val keyAlias: String by rootProject.extra
    val keyPassword: String by rootProject.extra
    val firebaseAppId: String by rootProject.extra
    val serviceCredentialsFilePath: String by rootProject.extra

    signingConfigs {
        create("release") {
            this.storeFile = file(keystoreFilePath)
            this.storePassword = keystorePassword
            this.keyAlias = keyAlias
            this.keyPassword = keyPassword
        }
    }

    namespace = "com.spotapp.mobile"

    defaultConfig {

        applicationId = "com.spotapp.mobile"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        configurations.all {
            resolutionStrategy {
                force("androidx.emoji2:emoji2-views-helper:1.3.0")
                force("androidx.emoji2:emoji2:1.3.0")
            }
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

        debug {
            firebaseAppDistribution {
                appId = firebaseAppId
                serviceCredentialsFile = serviceCredentialsFilePath
                artifactType = "APK"
                artifactPath = "$projectDir/build/outputs/apk/debug/app-debug.apk"
                groups = "qa"
            }
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    koverReport {
        filters {
            excludes {
                classes(
                    "*_Impl*",
                    "*Activity",
                    "*Application*",
                    "*.BuildConfig"
                )
                packages(
                    "android.*"
                )
            }
        }

        defaults {

            mergeWith("debug")

            filters {
                excludes {
                    classes(
                        "*_Impl*",
                        "*Activity",
                        "*Application*",
                        "*.BuildConfig"
                    )
                    packages(
                        "android.*"
                    )
                }
            }

            xml {
                onCheck = true
            }

            html {
                onCheck = true
            }
        }
    }
}

dependencies {

    kover(project(":data"))
    kover(project(":domain"))
    kover(project(":ui"))

    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":ui"))

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}

apply("$rootDir/config.gradle")
