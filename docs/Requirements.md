# Requirements

## Requirements (as epics)

- [ ] Initial setup  (see below)
- [ ] Authentication & Authorization (see below)
- [ ] Inform time entries  (see below)
- [ ] Listing of my own time entries
- [ ] Invoicing report  (see below)

## Definition of Done for functional requirements

- [ ] The feature is implemented
- [ ] The feature is made available in the HTTP API
- [ ] Any needed database change is written as a migration that can be run automatically by our deployment chain
- [ ] If/When security is already implemented, the feature authorization is implemented
- [ ] There are automated tests of all the domain (with test doubles for repositories)
- [ ] There are automated integration tests for any new operation in the driven adapters
- [ ] There are automated tests for any new endpoint in the HTTP API (either using the domain or mocking it)
- [ ] There are automated tests for the feature authorization (either at the HTTP API level or the domain leve)

### Not in list

- We assume migrations can't be reverted automatically. Let's discuss whether that is doable in the future and assume 
  the risks.
- We assume there is no UI nor any other form of access to the app other than the HTTP API. These could be added as
  new features in the future.

## Initial setup

- [x] The application is deployed in Heroku
- [x] The application is usable via an HTTP API in Json
- [ ] Remove the example console application
- [ ] The application runs the migrations on each deployment / start up

### Inform time entries

- [ ] A user can upload a file with the time entries in a period in Toggle format
- [ ] A user can upload a file with the time entries in a period in Tyme format
- [ ] The user time entries are validated or else an error is returned
  - Time entries must not overlap
  - Time entries can't amount to more than 12 hours per day (parameterizable)
- [ ] A user can re-upload a file with time entries and replace previous time entries in the same period

### Invoicing report

- [ ] Get a report of total hours for a month (or between dates) broken down by project and developer

### Authentication & Authorization 

- [ ] Only authorized users can use the application
- [ ] A user is associated with a developer name and she can only upload her own time entries
- [ ] Only certain users can consult information about other developers
- [ ] User credentials / tokens and roles are stored securely
  - OUT OF SCOPE: An API/UI to manage users, their credentials and roles. We assume we can store them by hand in the DB.
