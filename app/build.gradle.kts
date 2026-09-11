plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }

android { namespace = "nl.sebastiaanvogels.iphonelauncher"; compileSdk = 35
    defaultConfig { applicationId = "nl.sebastiaanvogels.iphonelauncher"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0" }
}

kotlin { jvmToolchain(17) }
