import Dependencies.hikariCp
import Dependencies.kotestRunnerJunit
import Dependencies.ktorServerNetty
import Dependencies.postgresql
import Dependencies.slf4jProvider
import Dependencies.suspendApp
import Dependencies.suspendAppKtor

plugins {
    application
    id("java")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.agilogy.timetracking.app.ApiServerKt"
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
    mainClass.set("com.agilogy.timetracking.app.AppKt")
}

dependencies {
    implementation(slf4jProvider)
    implementation(postgresql)
    implementation(ktorServerNetty)
    implementation(project(":db"))
    implementation(project(":domain"))
    implementation(project(":postgresdb"))
    implementation(project(":httpapi"))
    implementation(suspendApp)
    implementation(suspendAppKtor)
    implementation(hikariCp)
    testImplementation(kotestRunnerJunit)
}
