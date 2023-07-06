 plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.iflytek.cyber.evs.sdk"
    compileSdk = 33

    defaultConfig {
        minSdk = 19
        targetSdk = 33

        externalNativeBuild {
//            cmake {
//                cFlags = "-fPIC -O2 -fpermissive -Wint-to-pointer-cast -fvisibility=hidden -Wl,-Bsymbolic"
//                cppFlags = "-fPIC -O2 -fpermissive -Wint-to-pointer-cast -fvisibility=hidden -Wl,-Bsymbolic"
//            }
//            ndk {
//                abiFilters "armeabi", "armeabi-v7a", "arm64-v8a", "mips", "mips64", "x86", "x86_64"
//            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    externalNativeBuild {
        cmake {
            path(file("src/main/jni/src/codec/CMakeLists.txt"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")

    implementation("io.socket:socket.io-client:1.0.0")

    implementation("com.alibaba:fastjson:1.2.41")

    // for players
    implementation("com.google.android.exoplayer:exoplayer:2.10.1")
    implementation("com.google.android.exoplayer:exoplayer-core:2.10.1")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.10.1")
    implementation("com.google.android.exoplayer:exoplayer-smoothstreaming:2.10.1")
    implementation("com.google.android.exoplayer:exoplayer-hls:2.10.1")

    // okhttp
    implementation("com.squareup.okhttp3:okhttp:3.12.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:3.11.0")
}
