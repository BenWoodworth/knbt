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
    kotlin("multiplatform") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.7.1"
    id("org.jetbrains.dokka") version "1.5.0"
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

    linuxX64()
    macosX64()
    iosArm64()
    iosX64()
    watchosArm32()
    watchosArm64()
    watchosX86()
    mingwX64()

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
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinx_serialization_version")
                implementation("com.squareup.okio:okio-multiplatform:$okio_version")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
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
                    name.set("The GNU General Public License")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
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
