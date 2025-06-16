plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hanoistudentgigs"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hanoistudentgigs"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // Bật tính năng View Binding để thay thế findViewById, giúp code an toàn và ngắn gọn hơn (Tùy chọn)
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase Bill of Materials (BOM)
    implementation(platform(libs.firebase.bom))

    // Firebase SDKs
    implementation(libs.com.google.firebase.firebase.auth)
    implementation(libs.com.google.firebase.firebase.firestore)
    implementation(libs.com.google.firebase.firebase.storage)

    // FirebaseUI for Firestore (giúp làm việc với RecyclerView dễ dàng)
    implementation(libs.firebase.ui.firestore)
    implementation (libs.firebase.firestore)


    // Thư viện AndroidX và Material Design
    implementation(libs.appcompat.v161)
    implementation(libs.material)
    implementation(libs.constraintlayout.v214)
    implementation(libs.activity.v190)

    // Thư viện cho ViewPager2 (dùng cho Tab Layout của Admin)
    implementation(libs.viewpager2)

    implementation (libs.circleimageview)
    implementation (libs.cardview)

    implementation (libs.material.v160)
    implementation (libs.circleimageview)


    // Thư viện load ảnh (tùy chọn nhưng rất hữu ích)
    implementation(libs.picasso.v28)
    implementation(libs.firebase.database)

    // Thư viện Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.v115)
    androidTestImplementation(libs.espresso.core.v351)
}
