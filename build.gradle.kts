import groovy.json.JsonSlurper
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Base64

val kotlinx_serialization_version: String by extra
val okio_version: String by extra

System.getenv("GIT_REF")?.let { gitRef ->
    Regex("refs/tags/v(.*)").matchEntire(gitRef)?.let { gitVersionMatch ->
        version = gitVersionMatch.groupValues[1]
    }
}

val isSnapshot = version.toString().contains("SNAPSHOT", true)

plugins {
    kotlin("multiplatform") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.16.3"
    id("org.jetbrains.dokka") version "2.0.0"
    id("maven-publish")
    id("signing")
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
                implementation("com.benwoodworth.parameterize:parameterize:0.3.3")
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
}

dokka {
    dokkaSourceSets.all {
        documentedVisibilities = setOf(VisibilityModifier.Public)
        skipDeprecated = true
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier = "javadoc"
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

val mavenCentralPortalUsername: String? = System.getenv("MAVEN_CENTRAL_PORTAL_USERNAME")
val mavenCentralPortalPassword: String? = System.getenv("MAVEN_CENTRAL_PORTAL_PASSWORD")

publishing {
    repositories {
        maven {
            val releasesUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            val snapshotsUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
            url = if (isSnapshot) snapshotsUrl else releasesUrl

            credentials {
                username = mavenCentralPortalUsername
                password = mavenCentralPortalPassword
            }
        }
    }

    publications.withType<MavenPublication> {
        artifact(javadocJar.get())

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
}

/**
 * OSSRH Staging API: [`POST /manual/upload/defaultRepository`](https://ossrh-staging-api.central.sonatype.com/swagger-ui/#/default/manual_upload_default_repository)
 *
 * This endpoint is intended to support the usecase of publishing to OSSRH via the "Maven style" PUT-based API. That API
 * does not open a staging repository before sending files, so OSSRH's behavior was to create a default repository based
 * on the files that were uploaded. Publishers will need to call this endpoint in order to make the deployment available
 * in the Portal for their manual testing.
 *
 * @param namespace The namespace of the repository. Can be found by visiting https://central.sonatype.com/publishing/namespaces.
 */
fun ossrhStagingApiUpload(namespace: String, username: String, password: String): OssrhStagingApiUploadResult {
    val baseUrl = "https://ossrh-staging-api.central.sonatype.com"
    val namespaceUrlParameter = URLEncoder.encode(namespace, StandardCharsets.UTF_8.name())
    val url = URL("$baseUrl/manual/upload/defaultRepository/$namespaceUrlParameter")

    val token = Base64.getEncoder().encodeToString("$username:$password".encodeToByteArray())

    logger.debug("POST {}", url)
    val connection = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        setRequestProperty("Authorization", "Bearer $token")
    }

    try {
        logger.debug("Response: ${connection.responseMessage}")

        return when (connection.responseCode) {
            HttpURLConnection.HTTP_BAD_REQUEST -> {
                // Response schema: { "error": "string" }
                val response = connection.errorStream.use { JsonSlurper().parse(it) }
                check(response is Map<*, *>) { "Response is not an object. Got ${response::class.simpleName}." }

                val errorEntry = response.entries.firstOrNull { it.key == "error" }
                checkNotNull(errorEntry) { "Response object did not contain 'error'." }

                val error = errorEntry.value
                check(error is String) { "Response error was not a string. Got ${error?.javaClass?.simpleName}." }

                OssrhStagingApiUploadResult.FailedUpload(error)
            }

            HttpURLConnection.HTTP_OK -> OssrhStagingApiUploadResult.SuccessfulUpload
            HttpURLConnection.HTTP_UNAUTHORIZED -> OssrhStagingApiUploadResult.Unauthorized
            else -> error("Unhandled response: ${connection.responseMessage}")
        }
    } finally {
        connection.disconnect()
    }
}

sealed interface OssrhStagingApiUploadResult {
    object SuccessfulUpload : OssrhStagingApiUploadResult
    data class FailedUpload(val error: String) : OssrhStagingApiUploadResult
    object Unauthorized : OssrhStagingApiUploadResult
}

tasks.register("uploadOssrhStagingApiDeployment") {
    group = "publishing"
    description = "Upload OSSRH Staging API deployment to the Central Publisher Portal"
    onlyIf("Only needed for releases, which use the Central Publishing Portal") { !isSnapshot }

    doLast {
        val namespace = project.group.toString()
        val username = checkNotNull(mavenCentralPortalUsername) { "Publishing username is not set" }
        val password = checkNotNull(mavenCentralPortalPassword) { "Publishing password is not set" }

        logger.lifecycle("Uploading OSSRH Staging API deployment to Central Publisher Portal")
        when (val result = ossrhStagingApiUpload(namespace, username, password)) {
            OssrhStagingApiUploadResult.SuccessfulUpload -> logger.lifecycle("Successful upload")
            is OssrhStagingApiUploadResult.FailedUpload -> throw GradleException("Failed upload: ${result.error}")
            OssrhStagingApiUploadResult.Unauthorized -> throw GradleException("Unable to upload: Unauthorized")
        }
    }
}
