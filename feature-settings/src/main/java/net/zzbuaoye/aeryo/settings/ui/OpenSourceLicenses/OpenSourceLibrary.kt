package net.zzbuaoye.aeryo.settings.ui.OpenSourceLicenses

data class OpenSourceLibrary(
    val name: String,
    val author: String,
    val version: String,
    val packageName: String,
    val licenseType: String,
    val url: String,
    val licenseText: String
)

val OPEN_SOURCE_LIBRARIES = listOf(
    OpenSourceLibrary(
        name = "MIUIX KMP",
        author = "yukonga",
        version = "0.9.3",
        packageName = "top.yukonga.miuix.kmp:miuix-ui",
        licenseType = "Apache-2.0 / MIT",
        url = "https://github.com/compose-miuix-ui/miuix",
        licenseText = """
Copyright (c) 2024-2026 yukonga

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "Jetpack Compose",
        author = "Google LLC & AOSP",
        version = "1.7.0 (BOM 2026.06.01)",
        packageName = "androidx.compose.ui:ui",
        licenseType = "Apache-2.0",
        url = "https://developer.android.com/jetpack/compose",
        licenseText = """
Copyright (C) 2019-2026 The Android Open Source Project

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "Kotlin & Kotlinx Coroutines",
        author = "JetBrains s.r.o.",
        version = "1.8.1 (Kotlin 2.0.21)",
        packageName = "org.jetbrains.kotlinx:kotlinx-coroutines-android",
        licenseType = "Apache-2.0",
        url = "https://github.com/JetBrains/kotlin",
        licenseText = """
Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "Tabler Icons (Compose Icons)",
        author = "Paweł Kuna & DevsRSouza",
        version = "1.1.0",
        packageName = "br.com.devsrsouza.compose.icons:tabler-icons",
        licenseType = "MIT",
        url = "https://github.com/tabler/tabler-icons",
        licenseText = """
Copyright (c) 2020-2026 Paweł Kuna

$MIT_LICENSE_FULL
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "Coil Compose",
        author = "Coil Contributors",
        version = "2.7.0",
        packageName = "io.coil-kt:coil-compose",
        licenseType = "Apache-2.0",
        url = "https://github.com/coil-kt/coil",
        licenseText = """
Copyright 2026 Coil Contributors

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "Room Database & SQLite",
        author = "Google LLC & AOSP",
        version = "2.8.4",
        packageName = "androidx.room:room-runtime",
        licenseType = "Apache-2.0",
        url = "https://developer.android.com/training/data-storage/room",
        licenseText = """
Copyright (C) 2017-2026 The Android Open Source Project

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "AndroidX Biometric & Security Crypto",
        author = "Google LLC & AOSP",
        version = "1.2.0-alpha05",
        packageName = "androidx.biometric:biometric-ktx",
        licenseType = "Apache-2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/biometric",
        licenseText = """
Copyright (C) 2018-2026 The Android Open Source Project

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "Compose Reorderable",
        author = "Calvin Liang",
        version = "2.5.1",
        packageName = "sh.calvin.reorderable:reorderable",
        licenseType = "Apache-2.0",
        url = "https://github.com/aclassen/ComposeReorderable",
        licenseText = """
Copyright 2022-2026 Calvin Liang

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "Android Jetpack DataStore",
        author = "Google LLC & AOSP",
        version = "1.1.1",
        packageName = "androidx.datastore:datastore-preferences",
        licenseType = "Apache-2.0",
        url = "https://developer.android.com/topic/libraries/architecture/datastore",
        licenseText = """
Copyright (C) 2020-2026 The Android Open Source Project

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "AndroidX Navigation Compose",
        author = "Google LLC & AOSP",
        version = "2.8.0",
        packageName = "androidx.navigation:navigation-compose",
        licenseType = "Apache-2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/navigation",
        licenseText = """
Copyright (C) 2018-2026 The Android Open Source Project

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "AndroidX Lifecycle",
        author = "Google LLC & AOSP",
        version = "2.8.5",
        packageName = "androidx.lifecycle:lifecycle-runtime-ktx",
        licenseType = "Apache-2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/lifecycle",
        licenseText = """
Copyright (C) 2017-2026 The Android Open Source Project

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "AndroidX Core KTX & Activity",
        author = "Google LLC & AOSP",
        version = "1.13.1 / 1.13.0",
        packageName = "androidx.core:core-ktx",
        licenseType = "Apache-2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/core",
        licenseText = """
Copyright (C) 2018-2026 The Android Open Source Project

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    ),
    OpenSourceLibrary(
        name = "AndroidX SwipeRefreshLayout",
        author = "Google LLC & AOSP",
        version = "1.1.0",
        packageName = "androidx.swiperefreshlayout:swiperefreshlayout",
        licenseType = "Apache-2.0",
        url = "https://developer.android.com/jetpack/androidx/releases/swiperefreshlayout",
        licenseText = """
Copyright (C) 2018-2026 The Android Open Source Project

$APACHE_2_0_FULL_LICENSE
        """.trimIndent()
    )
)
