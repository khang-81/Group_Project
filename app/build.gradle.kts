plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Tên plugin Firestore
}

android {
    namespace = "com.example.hanoistudentgigs"
    compileSdk = 35

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
    // Firebase Bill of Materials (BOM) - LUÔN LUÔN ĐẶT ĐẦU TIÊN KHI SỬ DỤNG BOM
    implementation(platform(libs.firebase.bom))

    // Firebase SDKs (không cần chỉ định version vì đã có BOM)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database) // Đã có rồi, không cần lặp lại

    // FirebaseUI for Firestore (cần chỉ định version nếu không dùng BOM cho nó)
    implementation(libs.firebase.ui.firestore)


    // Thư viện AndroidX và Material Design
    implementation(libs.appcompat) // Sử dụng tên alias ngắn gọn nếu đã khai báo trong libs.versions.toml
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity)
    implementation(libs.viewpager2)
    implementation(libs.cardview) // CardView thường là androidx.cardview:cardview:1.0.0


    // Thư viện load ảnh (Picasso)
    implementation(libs.picasso) // Chỉ dùng một lần, bỏ dòng 'com.squareup.picasso:picasso:2.71828'
    implementation(libs.circleimageview) // CircleImageView


    // Thư viện Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext) // Đổi từ libs.junit.v115 sang tên alias chính xác
    androidTestImplementation(libs.espresso.core) // Đổi từ libs.espresso.core.v351 sang tên alias chính xác
}

tasks.withType<Test> {
    enabled = false
}