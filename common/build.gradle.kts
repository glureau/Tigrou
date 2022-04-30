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
                api("io.ktor:ktor-client-core:2.0.1")
                api("io.ktor:ktor-client-cio:2.0.1")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("io.github.pdvrieze.xmlutil:core:0.84.1")
                implementation("io.github.pdvrieze.xmlutil:serialization:0.84.1")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.github.pdvrieze.xmlutil:core-jvm:0.84.1")
                implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.84.1")
            }
        }
    }
}
