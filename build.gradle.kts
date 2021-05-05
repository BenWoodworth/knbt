val kotlinx_serialization_version: String by extra
val okio_version: String by extra

plugins {
    kotlin("multiplatform") version "1.5.0"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.4.0"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "net.benwoodworth"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
    }
    js {
        browser()
        nodejs()
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        configureEach {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
                useExperimentalAnnotation("net.benwoodworth.knbt.InternalNbtApi")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinx_serialization_version")
                implementation("com.squareup.okio:okio-multiplatform:$okio_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("reflect"))
            }
        }
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}
