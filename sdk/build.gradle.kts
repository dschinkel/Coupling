import com.moowork.gradle.node.task.NodeTask
import com.zegreatrob.coupling.build.BuildConstants
import com.zegreatrob.coupling.build.UnpackGradleDependenciesTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-serialization") version "1.3.50"
    id("smol-js")
}

kotlin {

    js {
        compilations {
            val endpointTest by compilations.creating
        }
    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(project(":model"))
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${BuildConstants.kotlinVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.2")
                implementation("com.soywiz:klock:1.1.1")
                implementation("io.github.microutils:kotlin-logging-common:1.7.6")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.13.0")
            }
        }
        getByName("commonTest") {
            dependencies {
                implementation(project(":json"))
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
                implementation("com.zegreatrob.testmints:standard:+")
                implementation("com.zegreatrob.testmints:minassert:+")
                implementation(project(":test-logging"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(project(":json"))
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${BuildConstants.kotlinVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.2")
                implementation("io.github.microutils:kotlin-logging-js:1.7.6")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.13.0")
            }
        }

        val jsEndpointTest by getting {
            dependsOn(jsMain)
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js")
                implementation("com.zegreatrob.testmints:standard:+")
                implementation("com.zegreatrob.testmints:minassert:+")
                implementation("com.zegreatrob.testmints:async-js:+")
                implementation("com.benasher44:uuid-js:0.0.5")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js")
                implementation("com.benasher44:uuid-js:0.0.5")
            }
        }
    }
}

tasks {

    val compileKotlinJs by getting(Kotlin2JsCompile::class) {
        kotlinOptions.moduleKind = "commonjs"
        kotlinOptions.sourceMap = true
        kotlinOptions.sourceMapEmbedSources = "always"
        kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
    }
    val compileTestKotlinJs by getting(Kotlin2JsCompile::class) {
        kotlinOptions.moduleKind = "commonjs"
        kotlinOptions.sourceMap = true
        kotlinOptions.sourceMapEmbedSources = "always"
        kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
    }

    val compileEndpointTestKotlinJs by getting(Kotlin2JsCompile::class) {
        kotlinOptions.moduleKind = "commonjs"
        kotlinOptions.sourceMap = true
        kotlinOptions.sourceMapEmbedSources = "always"
        kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
    }

    val unpackJsGradleDependencies by getting(UnpackGradleDependenciesTask::class) {
        dependsOn(":json:assemble")
        dependsOn(":test-logging:assemble")
    }

    val endpointTest by creating(NodeTask::class) {
        dependsOn("yarn", unpackJsGradleDependencies, compileEndpointTestKotlinJs, ":server:build")
        val script = projectDir.path + "/endpoint-wrapper.js"
        inputs.file(script)
        inputs.file(file("package.json"))

        setScript(File(script))
        outputs.dir("build/test-results/jsTest")
    }
    val check by getting {
        dependsOn(endpointTest)
    }

    afterEvaluate {
        val compileKotlinJsTasks = tasks.filterIsInstance(Kotlin2JsCompile::class.java)
        val processResources = tasks.filterIsInstance(ProcessResources::class.java)

        with(endpointTest) {
            dependsOn(compileKotlinJsTasks)
            dependsOn(processResources)

            val relevantPaths = listOf(
                "node_modules",
                "build/node_modules_imported",
                "../server/build/node_modules_imported"
            ) + compileKotlinJsTasks.map { it.outputFile.parent } + processResources.map { it.destinationDir.path }

            inputs.files(compileEndpointTestKotlinJs.outputFile)

            relevantPaths.forEach { if (File(it).isDirectory) inputs.dir(it) }

            setEnvironment(mapOf("NODE_PATH" to relevantPaths.joinToString(":")))

            setArgs(listOf("${compileEndpointTestKotlinJs.outputFile}"))
        }
    }

}
