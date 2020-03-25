import com.moowork.gradle.node.task.NodeTask
import com.moowork.gradle.node.yarn.YarnTask
import com.zegreatrob.coupling.build.BuildConstants
import com.zegreatrob.coupling.build.loadPackageJson
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("js")
    id("com.github.node-gradle.node")
    id("kotlinx-serialization") version "1.4-M1"
}

node {
    version = BuildConstants.nodeVersion
    npmVersion = BuildConstants.npmVersion
    yarnVersion = BuildConstants.yarnVersion
    download = true
}

kotlin {
    target {
        nodejs {
        }
        compilations {
            val endToEndTest by compilations.creating
        }
    }

    sourceSets {
        val test by getting {
            dependencies {
                implementation(npm("uuid", "^3.3.2"))
            }
        }

        val endToEndTest by getting {
            dependsOn(test)

            dependencies {
                implementation(project(":sdk"))
                implementation(npm("axios-cookiejar-support", "^0.5.0"))
                implementation(npm("tough-cookie", "^3.0.1"))
                implementation(npm("uuid", "^3.3.2"))
            }
        }
    }
}

val packageJson = loadPackageJson()

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":json"))
    implementation(project(":mongo"))
    implementation(project(":dynamo"))
    implementation(project(":repository:compound"))
    implementation(project(":repository:memory"))
    implementation(project("server_action"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.5-1.4-M1")
    implementation("com.soywiz.korlibs.klock:klock:1.8.9")
    implementation("io.github.microutils:kotlin-logging-js:1.7.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0-1.4-M1")

    packageJson.dependencies().forEach {
        implementation(npm(it.first, it.second.asText()))
    }

    testImplementation(kotlin("test-js"))
    testImplementation(project(":test-logging"))
    testImplementation("com.zegreatrob.testmints:standard:+")
    testImplementation("com.zegreatrob.testmints:minassert:+")
    testImplementation("com.zegreatrob.testmints:async-js:+")

    packageJson.devDependencies().forEach {
        testImplementation(npm(it.first, it.second.asText()))
    }
}

tasks {
    val yarn by getting {
        inputs.file(file("package.json"))
        outputs.dir(file("node_modules"))
    }

    val clean by getting {
        doLast {
            delete(file("build"))
        }
    }

    val compileKotlinJs by getting(Kotlin2JsCompile::class) {
        kotlinOptions.moduleKind = "umd"
        kotlinOptions.sourceMap = true
        kotlinOptions.sourceMapEmbedSources = "always"
    }

    val compileTestKotlinJs by getting(Kotlin2JsCompile::class) {
        kotlinOptions.moduleKind = "umd"
        kotlinOptions.sourceMap = true
        kotlinOptions.sourceMapEmbedSources = "always"
    }

    val compileEndToEndTestKotlinJs by getting(Kotlin2JsCompile::class) {
        kotlinOptions.moduleKind = "commonjs"
        kotlinOptions.sourceMap = true
        kotlinOptions.sourceMapEmbedSources = "always"
    }

    val copyServerIcons by creating(Copy::class) {
        from("public")
        into("build/executable/public")
    }

    val copyServerViews by creating(Copy::class) {
        from("views")
        into("build/executable/views")
    }

    val copyServerResources by creating {
        dependsOn(copyServerIcons, copyServerViews)
    }

    val copyClient by creating(Copy::class) {
        dependsOn(":client:assemble", copyServerResources)
        from("../client/build/distributions")
        into("build/executable/public/app/build")
    }

    val serverCompile by creating(YarnTask::class) {
        dependsOn(yarn, copyServerResources, compileKotlinJs)
        mustRunAfter(clean)
        inputs.file(compileKotlinJs.outputFile)
        inputs.dir("node_modules")
        inputs.file(file("package.json"))
        inputs.file(file("tsconfig.json"))
        inputs.file(file("webpack.config.js"))
        inputs.dir("config")
        inputs.dir("lib")
        inputs.dir("public")
        inputs.dir("routes")
        inputs.dir("views")
        inputs.file("app.ts")
        outputs.dir(file("build/executable"))
        setEnvironment(mapOf("NODE_ENV" to "production"))
        args = listOf("webpack", "--config", "webpack.config.js")
    }

    val assemble by getting {
        dependsOn(serverCompile, copyClient)
    }

    val serverTest by creating(YarnTask::class) {
        dependsOn(
            yarn,
            compileKotlinJs,
            compileTestKotlinJs,
            copyClient
        )
        inputs.file(file("package.json"))
        inputs.files(serverCompile.inputs.files)
        inputs.files(serverCompile.outputs.files)
        inputs.dir("test/unit")
        outputs.dir("build/test-results/server.unit")

        setEnvironment(mapOf("NODE_PATH" to "${rootProject.buildDir.path}/js/node_modules"))
        args = listOf("run", "serverTest")
    }

    val updateWebdriver by creating(YarnTask::class) {
        dependsOn(yarn)
        inputs.file("package.json")
        outputs.dir("node_modules/webdriver-manager/selenium/")
        args = listOf("run", "update-webdriver", "--silent")
    }

    val endToEndTest by creating(YarnTask::class) {
        dependsOn(assemble, updateWebdriver, compileEndToEndTestKotlinJs)
        mustRunAfter(serverTest, ":client:test", ":sdk:endpointTest")
        inputs.files(findByPath(":client:test")?.inputs?.files)
        inputs.files(findByPath(":client:assemble")?.outputs?.files)
        inputs.files(serverTest.inputs.files)
        inputs.files(serverCompile.outputs.files)
        inputs.files(compileEndToEndTestKotlinJs.outputs.files)
        inputs.file(file("package.json"))
        inputs.dir("test/e2e")
        outputs.dir("../test-output/e2e")

        setEnvironment(mapOf("NODE_PATH" to "${rootProject.buildDir.path}/js/node_modules"))
        args = listOf("run", "protractor", "--silent", "--seleniumAddress", System.getenv("SELENIUM_ADDRESS") ?: "")
    }

    val updateDependencies by creating(YarnTask::class) {
        dependsOn(yarn)
        args = listOf("run", "ncu", "-u")
    }

    val test by getting {
        dependsOn(serverTest)
    }

    val start by creating(YarnTask::class) {
        dependsOn(assemble)
        args = listOf("run", "start-built-app")
    }

    val testWatch by creating(NodeTask::class) {
        setArgs(listOf("test/continuous-run.js"))
    }

}