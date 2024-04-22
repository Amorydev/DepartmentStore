pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()

    }
}
dependencyResolutionManagement {
    repositories {
        google();
        maven { url = uri("https://jitpack.io")  }
        mavenCentral()

    }
}

rootProject.name = "DepartmentStore"
include(":app")
