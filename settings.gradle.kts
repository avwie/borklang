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
    ":compiler",
    ":lexer",
    ":parser",
    ":samples",
    ":vm"
)