import com.zegreatrob.coupling.build.BuildConstants
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("kotlinx-serialization") version "1.3.61"
}

kotlin {
    targets {
        jvm()
        js()
    }

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.Experimental")
        }
        getByName("commonMain") {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${BuildConstants.kotlinVersion}")
                implementation("io.github.microutils:kotlin-logging-common:1.7.8")
                implementation("com.soywiz:klock:1.1.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.14.0-1.3.60-eap-76")
            }
        }

        getByName("jsMain") {
            dependencies {
                implementation("io.github.microutils:kotlin-logging-js:1.7.8")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.14.0-1.3.60-eap-76")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js:${BuildConstants.kotlinVersion}")
            }
        }
    }
}

tasks {
    getByName<Kotlin2JsCompile>("compileKotlinJs") {
        kotlinOptions.moduleKind = "umd"
        kotlinOptions.sourceMap = true
        kotlinOptions.sourceMapEmbedSources = "always"
    }
}
