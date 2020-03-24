
import com.moowork.gradle.node.yarn.YarnTask
import com.zegreatrob.coupling.build.BuildConstants
import com.zegreatrob.coupling.build.loadPackageJson
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJsDce

plugins {
    kotlin("js")
    id("com.github.node-gradle.node")
}

node {
    version = BuildConstants.nodeVersion
    npmVersion = BuildConstants.npmVersion
    yarnVersion = BuildConstants.yarnVersion
    download = true
}

kotlin {
    target {
        browser {}
    }

    sourceSets {
        val main by getting {
            resources.srcDir("src/main/javascript")
        }
    }
}

val packageJson = loadPackageJson()

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":model"))
    implementation(project(":json"))
    implementation(project(":sdk"))
    implementation(project(":action"))
    implementation(project(":logging"))
    implementation("com.soywiz.korlibs.klock:klock:1.8.9")
    implementation("io.github.microutils:kotlin-logging-js:1.7.9")
    implementation("com.benasher44:uuid:0.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0-1.3.70-eap-274-2")
    implementation("org.jetbrains:kotlin-extensions:1.0.1-pre.93-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-css:1.0.0-pre.93-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-styled:1.0.0-pre.93-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-react:16.13.0-pre.93-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.93-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-react-router-dom:4.3.1-pre.93-kotlin-1.3.70")

    packageJson.dependencies().forEach {
        implementation(npm(it.first, it.second.asText()))
    }

    testImplementation(project(":stub-model"))
    testImplementation(project(":test-logging"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-common")
    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
    testImplementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
    testImplementation("com.zegreatrob.testmints:standard:+")
    testImplementation("com.zegreatrob.testmints:async-js:+")
    testImplementation("com.zegreatrob.testmints:minassert:+")

    val ignoredDependencies = listOf(
        "karma", "karma-webpack", "karma-sourcemap-loader", "karma-chrome-launcher", "webpack"
    )
    packageJson.devDependencies().forEach {
        if (!ignoredDependencies.contains(it.first))
            testImplementation(npm(it.first, it.second.asText()))
    }
}

val nodeEnv = System.getenv("COUPLING_NODE_ENV") ?: "production"

tasks {

    val clean by getting {
        doLast {
            delete(file("build/lib"))
            delete(file("build/report"))
        }
    }

    val compileKotlinJs by getting(Kotlin2JsCompile::class) {
        kotlinOptions.moduleKind = "commonjs"
        kotlinOptions.sourceMap = true
        kotlinOptions.sourceMapEmbedSources = "always"
    }
    val compileTestKotlinJs by getting(Kotlin2JsCompile::class) {
        kotlinOptions.moduleKind = "commonjs"
        kotlinOptions.sourceMap = true
        kotlinOptions.sourceMapEmbedSources = "always"
    }

    val processDceKotlinJs by getting(KotlinJsDce::class) {
    }

    val updateDependencies by creating(YarnTask::class) {
        dependsOn(yarn)
        args = listOf("run", "ncu", "-u")
    }

    forEach { if (!it.name.startsWith("clean")) it.mustRunAfter("clean") }

}
