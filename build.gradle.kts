import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val kotlinx_serialization_version: String by extra
val okio_version: String by extra
val parameterize_version: String by extra

System.getenv("GIT_REF")?.let { gitRef ->
    Regex("refs/tags/v(.*)").matchEntire(gitRef)?.let { gitVersionMatch ->
        version = gitVersionMatch.groupValues[1]
    }
}

val isSnapshot = version.toString().contains("SNAPSHOT", true)

plugins {
    kotlin("multiplatform") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.16.3"
    id("org.jetbrains.dokka") version "1.9.20"
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
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

    //wasmJs()   // Requires gzip/zlib support to be implemented
    //wasmWasi() //

    linuxX64()
    linuxArm64()
    //androidNativeArm32() // Not supported by Okio yet
    //androidNativeArm64() // https://github.com/square/okio/issues/1242#issuecomment-1759357336
    //androidNativeX86()   //
    //androidNativeX64()   //
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
                optIn("net.benwoodworth.knbt.MIGRATION Acknowledge that NbtCompound now has a stricter get")
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
                implementation("com.benwoodworth.parameterize:parameterize:$parameterize_version")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation(kotlin("test-junit5"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("pako", "2.0.3"))
            }
        }
        val jsTest by getting {
            dependencies {
                // https://github.com/square/okio/issues/1163
                implementation(devNpm("node-polyfill-webpack-plugin", "^2.0.1"))
            }
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}

apiValidation {
    nonPublicMarkers.add("net.benwoodworth.knbt.NbtDeprecated")
}

tasks.withType<DokkaTask> {
    dokkaSourceSets.all {
        includeNonPublic.set(false)
        skipDeprecated.set(true)
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

signing {
    gradle.taskGraph.whenReady {
        isRequired = allTasks.any { it is PublishToMavenRepository }
    }

    useInMemoryPgpKeys(
        System.getenv("SIGNING_KEY_ID"),
        System.getenv("SIGNING_KEY"),
        System.getenv("SIGNING_PASSWORD"),
    )

    sign(publishing.publications)
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_TOKEN")
            }
        }
    }

    publications.withType<MavenPublication> {
        artifact(javadocJar.get())

        pom {
            name.set("knbt")
            description.set("Minecraft NBT support for kotlinx.serialization")
            url.set("https://github.com/BenWoodworth/knbt")

            licenses {
                license {
                    name.set("GNU Lesser General Public License")
                    url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                }
            }
            developers {
                developer {
                    id.set("BenWoodworth")
                    name.set("Ben Woodworth")
                    email.set("ben@benwoodworth.net")
                }
            }
            scm {
                connection.set("scm:git:git://github.com:BenWoodworth/knbt.git")
                developerConnection.set("scm:git:ssh://github.com:BenWoodworth/knbt.git")
                url.set("https://github.com/BenWoodworth/knbt")
            }
        }
    }
}
