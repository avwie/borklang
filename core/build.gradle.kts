plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    js(IR) {
        browser()
    }

    jvm()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.betterParse)
                api(libs.jetbrains.serialization.json)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}