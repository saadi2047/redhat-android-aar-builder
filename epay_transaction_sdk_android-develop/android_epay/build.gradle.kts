import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    id("kotlin-parcelize")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "com.epay.sbi.android"
    compileSdk = 35

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        fun prop(key: String) = "\"${localProperties.getProperty(key, "")}\""

        // ✅ Added these three lines (required by SecuredUserAgent.kt, EpayWebView.kt)
        buildConfigField("String", "version_name", prop("version_name"))
        buildConfigField("String", "sk", prop("sk"))
        buildConfigField("String", "mName", prop("mName"))

        // Existing fields
        buildConfigField("String", "httpsProtocol", prop("httpsProtocol"))
        buildConfigField("String", "sbiDomainDev", prop("sbiDomainDev"))
        buildConfigField("String", "sbiDomainSit", prop("sbiDomainSit"))
        buildConfigField("String", "sbiDomainUat", prop("sbiDomainUat"))
        buildConfigField("String", "sbiDomainPreProd", prop("sbiDomainPreProd"))
        buildConfigField("String", "sbiDomainProd", prop("sbiDomainProd"))

        buildConfigField("String", "testPaymentUrlProtocolFail", prop("testPaymentUrlProtocolFail"))
        buildConfigField("String", "testPaymentUrlDomainFail", prop("testPaymentUrlDomainFail"))
        buildConfigField("String", "testPaymentUrlHashFail", prop("testPaymentUrlHashFail"))
        buildConfigField("String", "testPaymentUrlSuccess", prop("testPaymentUrlSuccess"))
        buildConfigField("String", "testLoadUrl", prop("LOAD_URL_DEV"))
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.auth)

    testImplementation(libs.junit)
    testImplementation(libs.core.ktx)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.espresso.web)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.cardview:cardview:1.0.0")
}

configurations.all {
    resolutionStrategy {
        force("androidx.core:core:1.12.0")
    }
}

