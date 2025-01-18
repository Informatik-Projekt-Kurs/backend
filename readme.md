# Backend Repository of MeetMate

## Setting up the application

Running the backend of MeetMate uses the docker engine for easy deployment.
All the following commands are to be executed in the root directory of the project.

### Running the application with Docker Compose

When the docker engine is running, start the application using
```
docker-compose up
```

This will run the docker compose file, download all necessary files, and configure the containers.

---

### Stopping the application

To stop the running application use either `Ctrl + C` in the terminal where the containers are running, stopping it in
the docker user interface or
```
docker-compose stop
```

To delete the created containers use:

```
docker-compose down
```

## Testing the endpoints

For testing we recommend using Postman. It allows to send single requests to each endpoint, complemented with a 
user-friendly interface it results in a easy testing experience. The endpoints in question are divided into RESTful and
GraphQL.

### Companies and Appointments

The queries and mutations of the companies and appointments are made using GraphQL. To test them you need to create a
GraphQL query to `localhost:8081/graphql`.

### Users

The user requests, using RESTful, are a bit more complicated. These all live on their own subdomain and need to have
their parameters set manually. While parameters are given in the format url form encoded data, the access tokens have to
be added into the "bearer" part of the authentication section.

| Method | Endpoint `localhost:8081`        | Parameters                    |
|--------|----------------------------------|-------------------------------|
| GET    | `/api/user/get`                  | access token*                 |
| POST   | `/api/user/signup`               | name*, email*, password*      |
| POST   | `/api/user/login`                | email*, password*             |
| POST   | `/api/user/refresh`              | refresh token*                |
| PUT    | `/api/user/update`               | access token*, name, password |
| DELETE | `/api/user/delete`               | access token*                 |
| PUT    | `/api/user/subscribe`            | access token*, companyId      |
| GET    | `/api/user/appointments`         | access token*                 |
| GET    | `/api/user/relevantAppointments` | access token*                 |

*values are required
