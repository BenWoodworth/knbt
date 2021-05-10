import org.jetbrains.dokka.gradle.DokkaTask

val kotlinx_serialization_version: String by extra
val okio_version: String by extra

plugins {
    kotlin("multiplatform") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.4.0"
    id("org.jetbrains.dokka") version "1.4.32"
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
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

tasks.withType<DokkaTask> {
    dokkaSourceSets.all {
        includeNonPublic.set(false)
        skipDeprecated.set(true)
    }
}

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().contains("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = properties["ossrh.username"]?.toString()
                password = properties["ossrh.password"]?.toString()
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

signing {
    sign(publishing.publications)
}
