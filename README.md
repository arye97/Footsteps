# SENG302 Team 600 Hakinakina Application
-----
This is the README file for the SENG302 Team 600 Hakinakina Application.

Hakinakina is a fitness and leisure tracking web application.  
It has been built using javascript - utilising Vue - and java - using Spring.

### Team Details
This project has been completed by Team600 from the SENG302 2020 class.

**Team members are**:
- Ryan Akers - rak80
- Saman Ighani - sig16
- Benjamin Thurber - bgt26
- Euan Widjaja - ewi45
- Taran Jennison - tmj49
- Rebekah McKinnon - rmc209
- Ryan Franks - ref48
- Hamish O'Keeffe - hoo42
- Ethan Burnett - ebb35

### Basic Project Structure
- client/src Frontend source code (JS - Vue)
- client/public publicly accesable web assets
- client/dist Frontend production build

- server/src Backend source code (Java - Spring)
- server/out Backend production build

### How to run
To run the full application, first run the server and then run the client
##### Server (Backend/API)
1. Set up the following environment variables:
 * SPRING_DATASOURCE_USERNAME
 * SPRING_DATASOURCE_PASSWORD
 * SPRING_DEFAULT_ADMIN_EMAIL
 * SPRING_DEFAULT_ADMIN_PASSWORD  
with the credentials for the database and default admin.  If you don't have access to these values, you can still run the application locally (see step 3).
2. Navigate to the server folder: 
Example command: `cd server`
3. Run the server
Command: `./gradlew bootRun`  
If you do not have access to the test database or default admin credentials, you can use an in memory database by using the command `./gradlew bootRun -PspringProfile=local` instead

Running on: http://localhost:9499/

Interfacing with the backend uses Postman with HTTP requests:
- Creating a user with a POST and the path: '/createprofile'
```
{
  "firstname": "Maurice",
  "middlename": "Jack",
  "lastname" : "DADSSA",
  "nickname": "Jacky",
  "email" : "mauricejacask@yahoo.com",
  "password": "jack",
  "bio": "Jacky loves to ride his bike on crazy mountains.",
  "date_of_birth": "1985-12-20",
  "gender": "male"
}
```
- User login with a GET and the path: '/login'
```
{
  "email" : "mauricejacask@yahoo.com",
  "password": "jack"
}
```
- Editing users with a POST by sending the id of the user to edit, and all attributes that are edited
```
{
  "id": "1",
  "bio": "I am a nice bio",
  "password": "I am a nice bio"
}
```

##### Client (Frotnend/GUI)
1. Navigate to the client folder
Example command: `cd client`
2. Install the dependencies
Command: `npm install`
3. Run the client:
Command: `npm run serve`

Running on: http://localhost:9500/

### Example User
An example user has been created.  
This can be accessed by logging in using the following credentials:

**Username**:  
**Password**:

### Third Party Dependencies
The Hakinakina program does use some external libraries. These are listed below.

**Given dependencies**:  
These were approved by the teaching staff before the project commenced

Backend:
- Spring Boot Data JPA Starter
- Spring Boot Data REST Starter
- Spring Boot Web Starter
- Project Lombok
- Spring Boot Test Starter
- Mockito
- H2 Database Engine
- Cucumber

Frontend:
- Axios
- Core-js
- Serve
- Vue
- Vuejs-logger
- @vue/cli-plugin-babel
- @vue/cli-plugin-eslint
- @vue/cli-service
- babel-eslint
- eslint
- eslint-plugin-vue
- vue-template-compiler
- jest
- @vue/test/utils

**Further approved dependencies**:  
These dependencies have been approved for use in the Hakinakina application

Backend:
- Spring Security
- MariaDB

Frontend:
- Bootstrap-Vue (and jQuery by association)
- Vue-router
- Vue-multiselect
- Vue-jest (and babel-core by association)
- @vue/test-utils
