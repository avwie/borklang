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
                api(projects.common)
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