import org.jetbrains.dokka.gradle.DokkaTask

val kotlinx_serialization_version: String by extra
val kotlinx_coroutines_version: String by extra
val okio_version: String by extra
val kotest_version: String by extra

System.getenv("GIT_REF")?.let { gitRef ->
    Regex("refs/tags/v(.*)").matchEntire(gitRef)?.let { gitVersionMatch ->
        version = gitVersionMatch.groupValues[1]
    }
}

val isSnapshot = version.toString().contains("SNAPSHOT", true)

plugins {
    kotlin("multiplatform") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.13.0"
    id("org.jetbrains.dokka") version "1.7.20"
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
    }

    js(IR) {
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

    linuxX64()
    macosX64()
    iosArm64()
    iosX64()
    watchosArm32()
    watchosArm64()
    watchosX86()
    mingwX64()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        configureEach {
            val isTest = name.endsWith("Test")

            languageSettings.apply {
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("net.benwoodworth.knbt.InternalNbtApi")
                optIn("net.benwoodworth.knbt.MIGRATION Acknowledge that NbtCompound now has a stricter get")

                if (isTest) optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinx_coroutines_version")
                implementation("io.kotest:kotest-property:$kotest_version")
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
        val nativeMain by creating {
            dependsOn(commonMain)
        }

        val linuxX64Main by getting { dependsOn(nativeMain) }
        val macosX64Main by getting { dependsOn(nativeMain) }
        val iosArm64Main by getting { dependsOn(nativeMain) }
        val iosX64Main by getting { dependsOn(nativeMain) }
        val watchosArm32Main by getting { dependsOn(nativeMain) }
        val watchosArm64Main by getting { dependsOn(nativeMain) }
        val watchosX86Main by getting { dependsOn(nativeMain) }
        val mingwX64Main by getting { dependsOn(nativeMain) }
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
