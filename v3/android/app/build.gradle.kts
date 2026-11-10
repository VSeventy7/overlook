plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
val overlook = rootProject.projectDir.resolve("../..")
android {
    namespace = "com.ansub.tvhome"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.ansub.tvhome"
        minSdk = 21
        targetSdk = 34
        versionCode = 29
        versionName = "1.8.0"
    }
    buildFeatures { buildConfig = true }
    buildTypes { release { isMinifyEnabled = false } }
    sourceSets.getByName("main") {
        java.srcDirs(overlook.resolve("src"), overlook.resolve("out/gen"), "src/main/java")
        res.srcDirs(overlook.resolve("res"), "src/main/res")
        manifest.srcFile(overlook.resolve("AndroidManifest.xml"))
        assets.srcDirs(overlook.resolve("assets"), overlook.resolve("out/helper-assets"), layout.buildDirectory.dir("generated/couch-assets"))
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    packaging { resources { excludes += setOf("META-INF/versions/9/OSGI-INF/MANIFEST.MF", "META-INF/*.kotlin_module") } }
}
dependencies {
    implementation("org.nanohttpd:nanohttpd:2.3.1")
    implementation("org.nanohttpd:nanohttpd-websocket:2.3.1")
    implementation("com.github.MuntashirAkon:libadb-android:3.1.1")
    implementation("org.conscrypt:conscrypt-android:2.5.3")
    implementation("org.bouncycastle:bcpkix-jdk15to18:1.81")
}
val couchAssets = tasks.register<Copy>("couchAssets") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    into(layout.buildDirectory.dir("generated/couch-assets"))
    from(overlook.resolve("vendor/couch-tv/tv-home/web")) { into("www/tv"); exclude("mock.js", "**/*.md", "**/.gitkeep") }
    from(overlook.resolve("vendor/couch-tv/on-tv/web")) { into("www/ontv") }
    from(overlook.resolve("vendor/couch-tv/remote/dist")) { into("www/remote") }
    from(overlook.resolve("v3/web")) { into("www/tv") }
    from(overlook.resolve("v3/licenses")) { into("www/tv/licenses") }
    from(overlook.resolve("vendor/couch-tv/LICENSE")) { into("www/tv"); rename { "COUCH-TV-LICENSE.txt" } }
}
tasks.named("preBuild") { dependsOn(couchAssets) }
