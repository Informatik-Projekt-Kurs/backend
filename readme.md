# Backend Repository of MeetMate

## Setting up the application

All the following commands are to be executed in the root directory of the project.

### Creating the Docker image
Make the jar file with<br>
> mvn clean package
---
### Running the application with Docker Compose
Start the application using 
>docker-compose up
 
_On first Startup the Application will probably fail to start because the database is not ready yet. Just restart the application with `docker-compose up` and it should work._

---
### Stopping the application
To stop the running application use either `Ctrl + C` or
>docker-compose stop

or delete the currently running containers with
>docker-compose down