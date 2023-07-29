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
                implementation(projects.parser)
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