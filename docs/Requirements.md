# Requirements

## Non functional requirements

- [ ] The application is deployed in a cloud provider
- [ ] The application is usable via an HTTP API in Json
- [ ] Remove the example console application

### The application is deployed in a cloud provider

- [ ] Deploy on Heroku
- [ ] Github actions to deploy on Heroku every time a PR is merged into main

## Functional Requirements (as epics)

- [ ] Inform time entries
- [ ] Listing of my own time entries
- [ ] Invoicing report
- [ ] Authentication & Authorization

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
  - OUT OF SCOPE: An API/UI to manage users, their credentials and roles
