import org.jetbrains.dokka.gradle.DokkaTask

val kotlinx_serialization_version: String by extra
val okio_version: String by extra

System.getenv("GIT_REF")?.let { gitRef ->
    Regex("refs/tags/v(.*)").matchEntire(gitRef)?.let { gitVersionMatch ->
        version = gitVersionMatch.groupValues[1]
    }
}

val isSnapshot = version.toString().contains("SNAPSHOT", true)

plugins {
    kotlin("multiplatform") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.14.0"
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
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
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
                implementation("com.benwoodworth.parameterize:parameterize:0.3.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("reflect"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("pako", "2.0.3"))
            }
        }
    }
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

    // https://github.com/gradle/gradle/issues/26091#issuecomment-1722947958
    tasks.withType<AbstractPublishToMaven>().configureEach {
        val signingTasks = tasks.withType<Sign>()
        mustRunAfter(signingTasks)
    }
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
