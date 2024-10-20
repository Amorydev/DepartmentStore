plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.amory.departmentstore"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.amory.departmentstore"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-messaging:24.0.2")
    implementation("com.google.firebase:firebase-firestore:25.1.0")
    implementation(fileTree(mapOf(
        "dir" to "D:\\ZPDK-Android",
        "include" to listOf("*.aar", "*.jar"),
    )))
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    //GSON Converter
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    //Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")

    //recyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    // For control over item selection of both touch and mouse driven selection
    implementation ("androidx.recyclerview:recyclerview-selection:1.1.0")

    //notification badge
    implementation ("com.nex3z:notification-badge:1.0.4")

    //Events Bus
    implementation ("org.greenrobot:eventbus:3.3.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    //ImageSLide
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    //paperDB
    implementation ("io.github.pilgr:paperdb:2.7.2")
    //circle ImageView
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    //Gson
    implementation ("com.google.code.gson:gson:2.10.1")
    //ImagePicker
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    //Skeleton loader
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    //Progress bar
    implementation ("io.github.litao0621:nifty-slider:1.4.6")
    //okhttp
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
    //ViewPager 2
    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    //SpinKit(Custom progressBar)
    implementation ("com.github.ybq:Android-SpinKit:1.4.0")
    //Pie Chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //viewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation ("androidx.lifecycle:lifecycle-service:2.8.6")
    implementation ("androidx.activity:activity-ktx:1.9.3")

    //LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    //Fragment
    implementation ("androidx.fragment:fragment-ktx:1.8.4")
    //Material
    implementation ("com.google.android.material:material:1.12.0")
    //Lottie Animation
    implementation ("com.airbnb.android:lottie:6.5.2")


}