plugins {
    kotlin("multiplatform")
    application
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.core)
            }
        }
    }
}

application {
    mainClass.set("nl.avwie.borklang.repl.REPLKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}