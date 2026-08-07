import java.util.Properties
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
// ==============================================================================
// Aeryo Version Information
// ==============================================================================
val defaultAeryoVersion = "1.0.3"
val defaultAeryoChannel = "Stable" // 可选: Stable, Beta, Alpha, RC 等
val defaultAeryoBuildRevision = "2"

// 优先读取 Gradle 命令行/属性参数 (-PaeryoVersion / -PaeryoChannel)，若无则使用上述默认值
val aeryoVersion = providers.gradleProperty("aeryoVersion")
    .orElse(defaultAeryoVersion)
    .get()
    .removePrefix("V")

val aeryoChannel = providers.gradleProperty("aeryoChannel")
    .orElse(defaultAeryoChannel)
    .get()
    .trim()

val aeryoBuildRevision = providers.gradleProperty("aeryoBuildRevision")
    .orElse(defaultAeryoBuildRevision)
    .get()
    .toIntOrNull()
    ?: error("aeryoBuildRevision must be a positive integer")

require(aeryoBuildRevision in 1..99) {
    "aeryoBuildRevision must be between 1 and 99"
}

val aeryoBuildDate = LocalDate.now(ZoneId.of("Asia/Shanghai"))
    .format(DateTimeFormatter.ofPattern("yyMMdd", Locale.ROOT))

val aeryoBuildLabel = "aeryo$aeryoBuildDate.$aeryoBuildRevision"
val aeryoAndroidVersionCode =
    aeryoBuildDate.toInt() * 100 + aeryoBuildRevision
val aeryoAndroidVersionName = if (aeryoChannel.isBlank() || aeryoChannel.equals("Stable", ignoreCase = true)) {
    aeryoVersion
} else {
    "$aeryoVersion-${aeryoChannel.lowercase(Locale.ROOT)}"
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    idea
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use(::load)
    }
}
val hasReleaseSigning = keystorePropertiesFile.exists() &&
    listOf("storeFile", "storePassword", "keyAlias", "keyPassword").all {
        !keystoreProperties.getProperty(it).isNullOrBlank()
    }

android {
    namespace = "net.zzbuaoye.aeryo"
    compileSdk = 36

    defaultConfig {
        applicationId = "net.zzbuaoye.aeryo"
        minSdk = 26
        targetSdk = 34
        versionCode = aeryoAndroidVersionCode
        versionName = aeryoAndroidVersionName
        manifestPlaceholders["aeryoDisplayVersion"] = aeryoVersion
        manifestPlaceholders["aeryoChannel"] = aeryoChannel
        manifestPlaceholders["aeryoBuildLabel"] = aeryoBuildLabel

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        encoding = "UTF-8"
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xencoding=UTF-8")
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core-ui"))
    implementation(project(":core-browser"))
    implementation(project(":feature-bookmarks"))
    implementation(project(":feature-downloads"))
    implementation(project(":feature-settings"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.navigation.compose)

    // MIUIX UI Library
    implementation(libs.miuix.ui)
    implementation(libs.miuix.preference)
    implementation(libs.miuix.icons)
    implementation(libs.miuix.shader)
    implementation(libs.miuix.squircle)
    // Room Storage
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.biometric:biometric-ktx:1.2.0-alpha05")
    implementation(libs.compose.reorderable)
    implementation(libs.compose.icons.tabler)
}
