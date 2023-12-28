import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
//dependencies {
//    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
//}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.7.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation(compose.desktop.currentOs)
                implementation("androidx.compose.ui:ui-test-junit4:1.3.0")
                implementation("androidx.compose.ui:ui-test-manifest:1.3.0")
                implementation("junit:junit:4.13.2")
                implementation("org.jetbrains.compose.ui:ui-test-junit4:1.2.1")
                implementation("androidx.compose.ui:ui-test-junit4:1.0.0")
            }
        }
        val jvmTest by getting
    }
}



compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ExecuteSwiftScript"
            packageVersion = "1.0.0"
        }
    }
}
