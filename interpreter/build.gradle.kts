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
                implementation(projects.common)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.samples)
                implementation(projects.parser)
            }
        }
    }
}