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
                implementation(projects.interpreter)
            }
        }
    }
}