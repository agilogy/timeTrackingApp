rootProject.name = "listings"

plugins {
    id("com.gradle.enterprise") version ("3.9")
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

rootProject.name = "timeTrackingApp"

fun String.configureProjects() =
    File("$rootDir/${this@configureProjects}/").listFiles()!!.filter { it.isDirectory }.map { it.name }.forEach {
        include(":$it")
        project(":$it").projectDir = File("${this@configureProjects}/$it")
    }

"components".configureProjects()
"libs".configureProjects()
"apps".configureProjects()
