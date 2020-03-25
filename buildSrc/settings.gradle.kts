pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
        maven ("https://kotlin.bintray.com/kotlinx")

    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-serialization") {
                useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
}
