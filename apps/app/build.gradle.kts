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

repositories {
    mavenCentral()
}

val myMainClass = "com.agilogy.timetracking.app.MainKt"

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = myMainClass
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
    mainClass.set(myMainClass)
}

dependencies {
    implementation(slf4jProvider)
    implementation(postgresql)
    implementation(ktorServerNetty)
    implementation(project(":db"))
    implementation(project(":domain"))
    implementation(project(":postgresdb"))
    implementation(project(":httpapi"))
    implementation(project(":herokupostgres"))
    implementation("net.dv8tion:JDA:5.0.0-alpha.22")
    implementation(suspendApp)
    implementation(suspendAppKtor)
    implementation(hikariCp)
    testImplementation(kotestRunnerJunit)
}
