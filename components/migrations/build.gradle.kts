import Dependencies.flywayCore
import Dependencies.hikariCp
import Dependencies.kotestRunnerJunit
import Dependencies.postgresql
import Dependencies.slf4jProvider

dependencies {
    implementation(project(":domain"))
    implementation(project(":db"))
    api(flywayCore)
    implementation(slf4jProvider)

    testImplementation(hikariCp)
    testImplementation(postgresql)
    testImplementation(kotestRunnerJunit)
    testImplementation(testFixtures(project(":domain")))

    testFixturesImplementation(project(":domain"))
}
