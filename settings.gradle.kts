pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EndToShare"

include(":app")
include(":core:designsystem")
include(":core:model")
include(":core:p2p:api")
include(":core:p2p:nfc")
include(":core:p2p:wifi-direct")
include(":core:p2p:hotspot")
include(":core:p2p:transfer")
include(":feature:home")
include(":core:p2p:session")
include(":core:p2p:security")
include(":core:p2p:file")
