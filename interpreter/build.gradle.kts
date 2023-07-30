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