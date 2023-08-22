plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        nodejs {
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.core)
            }
        }
    }
}