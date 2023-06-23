# Requirements

## Requirements (as epics)

- [ ] Initial setup  (see below)
- [ ] Authentication & Authorization (see below)
- [ ] Inform time entries  (see below)
- [ ] Listing of my own time entries
- [ ] Invoicing report  (see below)

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
  - OUT OF SCOPE: An API/UI to manage users, their credentials and roles
