plugins {
    kotlin("multiplatform")
    application
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.parser)
                implementation(projects.interpreter)
            }
        }
    }
}

application {
    mainClass.set("nl.avwie.borklang.repl.REPLKt")
}