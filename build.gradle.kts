import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPullImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.zegreatrob.coupling.build.JsonLoggingTestListener
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

plugins {
    id("com.github.node-gradle.node") apply false
    id("com.bmuschko.docker-remote-api") version "6.4.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.13"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("net.rdrei.android.buildtimetracker") version "0.11.0"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://dl.bintray.com/robertfmurdock/zegreatrob") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-js-wrappers") }
    }

    tasks {
        val copyReportsToCircleCIDirectory by creating(Copy::class) {
            from("build/reports")
            into("${rootProject.buildDir.path}/test-output/${project.path}")
        }
    }

    afterEvaluate {
        mkdir(file(rootProject.buildDir.toPath().resolve("test-output")))
        tasks.withType(KotlinJsTest::class) {
            addTestListener(JsonLoggingTestListener(path))
        }
    }

}

docker {
    registryCredentials {
        username.set(System.getenv("DOCKER_USER"))
        password.set(System.getenv("DOCKER_PASS"))
        email.set(System.getenv("DOCKER_EMAIL"))
    }
}

tasks {

    val copyEndpointTestResults by creating(Copy::class, copyForTask(findByPath(":sdk:endpointTest")) {
        from("sdk/build/test-results/jsTest")
        into("build/test-output/endpoint")
    })

    val copyEndToEndResults by creating(Copy::class, copyForTask(findByPath(":server:endToEndTest")) {
        from("server/build/test-results/e2e")
        into("build/test-output/e2e")
    })

    val copyEndToEndScreenshotResults by creating(Copy::class, copyForTask(findByPath(":server:endToEndTest")) {
        from("server/build/reports/e2e")
        into("build/test-output/e2e-screenshots")
    })

    val copyTestResultsForCircle by creating {
        dependsOn(
            copyEndpointTestResults,
            copyEndToEndResults,
            copyEndToEndScreenshotResults
        )
    }

    val pullProductionImage by creating(DockerPullImage::class) {
        image.set("zegreatrob/coupling:latest")
    }

    val buildProductionImage by creating(DockerBuildImage::class) {
        mustRunAfter("pullProductionImage")
        inputDir.set(file("./"))
        dockerFile.set(file("Dockerfile.prod"))
        remove.set(false)
        images.add("zegreatrob/coupling:latest")
    }

    val pushProductionImage by creating(DockerPushImage::class) {
        mustRunAfter("buildProductionImage")
        images.add("zegreatrob/coupling:latest")
    }

    val serverYarn = getByPath(":server:yarn")
    val clientYarn = getByPath(":client:yarn")
    serverYarn.mustRunAfter(clientYarn)
    val sdkYarn = getByPath(":sdk:yarn")
    sdkYarn.mustRunAfter(serverYarn)

    val test by creating {
        dependsOn(":server:test", ":client:test")
    }

    val check by getting {
        dependsOn(test, ":sdk:endpointTest", ":server:endToEndTest")
    }

    val build by getting {
        dependsOn(test, ":client:assemble", ":server:build")
    }

}

fun copyForTask(testTask: Task?, block: Copy.() -> Unit): Copy.() -> Unit {
    return {
        mustRunAfter(testTask)

        block()
        testTask?.finalizedBy(this)
    }
}

buildtimetracker {
    reporters {
        register("csv") {
            options.run {
                put("output", "build/times.csv")
                put("append", "true")
                put("header", "false")
            }
        }

        register("summary") {
            options.run {
                put("ordered", "false")
                put("threshold", "50")
                put("header", "false")
            }
        }

        register("csvSummary") {
            options.run {
                put("csv", "build/times.csv")
            }
        }
    }
}

tasks.withType<DependencyUpdatesTask> {
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}