pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// dependency resolution management를 PREFER_PROJECT로 변경하여 allprojects에서 추가한 레포지토리를 사용할 수 있게 함
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Subtrack"
include(":app")
