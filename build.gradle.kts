group = "nl.avwie.borklang"

allprojects {
    version = "0.0.1-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    kotlin("multiplatform").version(libs.versions.jetbrains.kotlin).apply(false)

    @Suppress("DSL_SCOPE_VIOLATION")
    kotlin("plugin.serialization").version(libs.versions.jetbrains.kotlin).apply(false)
}