import Dependencies.arrowCore
import Dependencies.arrowFxCoroutines
import Dependencies.kotestRunnerJunit
import Dependencies.kotlinXCoroutinesCore
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    (kotlin("jvm") version "1.8.21") apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2" apply false
    id("org.jetbrains.kotlinx.kover") version "0.7.0-Beta"
}

// Common traits to all the projects in the build. See
// https://docs.gradle.org/current/userguide/multi_project_builds.html#sec:defining_common_behavior
// We can't use type-safe accessors when configuring subprojects within the root build script. See
// https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:multi_project_builds and
// https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin_cross_project_configuration
allprojects {
    group = "com.agilogy"
    version = "1.0-SNAPSHOT"

    // TODO: Test code coverage report
    // TODO: Build (and publish) documentation for libraries

    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java-test-fixtures")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    // Optionally configure plugin
    configure<KtlintExtension> {
        version.set("0.48.2")
//        android.set(false)
        outputToConsole.set(true)
        reporters {
            reporter(ReporterType.PLAIN)
        }
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
            allWarningsAsErrors = true
            freeCompilerArgs = listOf("-progressive", "-opt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        "implementation"(arrowCore)
        "implementation"(arrowFxCoroutines)
        "implementation"(kotlinXCoroutinesCore)
        "testImplementation"(kotestRunnerJunit)
    }
}

kover {
    useKoverTool()
}

dependencies {
    File("$rootDir/components/").listFiles()!!.filter { it.isDirectory }.map { it.name }
        .forEach { kover(project(":$it")) }

    File("$rootDir/libs/").listFiles()!!.filter { it.isDirectory }.map { it.name }
        .forEach { kover(project(":$it")) }

    kover(project(":app"))
}

koverReport {
    filters {
        includes {
            classes("com.agilogy.*")
        }
    }
}
