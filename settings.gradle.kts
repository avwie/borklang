enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "bork-lang"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(
    ":common",
    ":compiler",
    ":interpreter",
    ":lexer",
    ":parser",
    ":samples",
    ":vm"
)