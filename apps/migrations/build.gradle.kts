import Dependencies.flywayCore
import Dependencies.kotestRunnerJunit
import Dependencies.postgresql
import Dependencies.slf4jProvider
import Dependencies.suspendApp

plugins {
    application
    id("java")
}

val mainClassCompleteName = "com.agilogy.timetracking.migrations.MigrateDbKt"

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassCompleteName
    }
    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies otherwise a "NoClassDefFoundError" error
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
application {
    mainClass.set(mainClassCompleteName)
}

dependencies {
    implementation(slf4jProvider)
    implementation(postgresql)
    implementation(flywayCore)
    implementation(project(":db"))
    implementation(project(":postgresdb"))
    implementation(project(":herokupostgres"))
    implementation(suspendApp)
    testImplementation(kotestRunnerJunit)
}
