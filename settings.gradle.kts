enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ExcuseMeSample"
include(":app", ":excuseme")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}