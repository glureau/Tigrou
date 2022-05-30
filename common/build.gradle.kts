import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "me.glureau"
version = "1.0"

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.ui)
                api(compose.uiTooling)
                api(compose.materialIconsExtended)
                api("io.ktor:ktor-client-core:2.0.1")
                api("io.ktor:ktor-client-cio:2.0.1")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                implementation("com.google.accompanist:accompanist-flowlayout:0.24.7-alpha")
                implementation("com.google.accompanist:accompanist-pager:0.24.7-alpha")
                implementation("com.google.accompanist:accompanist-pager-indicators:0.24.7-alpha")

                api("com.squareup.sqldelight:coroutines-extensions:1.5.3")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.github.furstenheim:copy_down:1.0")
                api("com.squareup.sqldelight:runtime-jvm:1.5.3")
                api("com.squareup.sqldelight:sqlite-driver:1.5.3")
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs
    }
}
