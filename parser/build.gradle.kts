plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.lexer)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.samples)
            }
        }
    }
}