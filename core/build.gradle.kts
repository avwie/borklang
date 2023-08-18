plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR)
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}