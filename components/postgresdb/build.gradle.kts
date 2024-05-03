import Dependencies.hikariCp
import Dependencies.kotestProperty
import Dependencies.kotestPropertyArbs
import Dependencies.kotestRunnerJunit
import Dependencies.postgresql

dependencies {
    implementation(project(":domain"))
    implementation(project(":db"))

    testImplementation(hikariCp)
    testImplementation(postgresql)
    testImplementation(kotestRunnerJunit)
    testImplementation(testFixtures(project(":domain")))
    testImplementation(project(":migrations"))

    testImplementation(kotestProperty)
    testImplementation(kotestPropertyArbs)

    testFixturesImplementation(project(":domain"))
}
