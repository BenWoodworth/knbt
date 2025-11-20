import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

val kotlinx_serialization_version: String by extra
val okio_version: String by extra

plugins {
    kotlin("multiplatform") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("org.jetbrains.dokka") version "2.1.0"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js {
        browser {
            testTask {
                useKarma {
                    useFirefoxHeadless()
                    useChromeHeadless()
                }
            }
        }
        nodejs()
    }

    //wasmJs() // Requires gzip/zlib support to be implemented
    //wasmWasi()

    linuxX64()
    linuxArm64()
    //androidNativeArm32() // Not supported by Okio yet
    //androidNativeArm64() // https://github.com/square/okio/issues/1242#issuecomment-1759357336
    //androidNativeX86()
    //androidNativeX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()
    watchosDeviceArm64()
    mingwX64()

    sourceSets {
        configureEach {
            languageSettings.apply {
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("net.benwoodworth.knbt.InternalNbtApi")
            }
        }

        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinx_serialization_version")
                implementation("com.squareup.okio:okio:$okio_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")
                implementation("com.benwoodworth.parameterize:parameterize-core:0.4.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("reflect"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("pako", "2.1.0"))
            }
        }
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        enabled = true
    }
}

dokka {
    dokkaPublications.configureEach {
        failOnWarning = true
    }

    dokkaSourceSets.all {
        documentedVisibilities = setOf(VisibilityModifier.Public)
        skipDeprecated = true
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name = "knbt"
        description = "Minecraft NBT support for kotlinx.serialization"
        url = "https://github.com/BenWoodworth/knbt"

        licenses {
            license {
                name = "GNU Lesser General Public License"
                url = "https://www.gnu.org/licenses/lgpl-3.0.txt"
            }
        }
        developers {
            developer {
                id = "BenWoodworth"
                name = "Ben Woodworth"
                email = "ben@benwoodworth.net"
            }
        }
        scm {
            connection = "scm:git:git://github.com:BenWoodworth/knbt.git"
            developerConnection = "scm:git:ssh://github.com:BenWoodworth/knbt.git"
            url = "https://github.com/BenWoodworth/knbt"
        }
    }
}
