# timeTrackingApp

An Agilogy School workshop.

## Build

To setup your tools:

- `./gradlew ktlintApplyToIdea` to configure your `.idea` folder with the code style
- `./gradlew addKtlintFormatGitPreCommitHook` to add a format pre-commit hook to your git repository

While developing:

- `./gradlew --continue ktlintFormat` to format code
- `./gradlew koverHtmlReport` to generate and open the test coverage report
