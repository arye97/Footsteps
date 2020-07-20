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
- client/public publicly accessible web assets
- client/dist Frontend production build

- server/src Backend source code (Java - Spring)
- server/out Backend production build

### How to run
To run the full application, first run the server and then run the client
##### Add Environment Variables
Set up the environment variables according to the information stored in eng-git

##### Server (Backend/API)
1. Set up the following environment variables:
    - SPRING_DATASOURCE_USERNAME
    - SPRING_DATASOURCE_PASSWORD
    - SPRING_DEFAULT_ADMIN_EMAIL
    - SPRING_DEFAULT_ADMIN_PASSWORD  
    With the credentials for the database and default admin.  If you don't have access to these values, you can still run the application locally (see step 3).  
2. Navigate to the server folder: Example command: `cd server`  
3. Run the server Command: `./gradlew bootRun`  
    - By default this will run the localDev profile. You may use a specific server profile using the command `./gradlew bootRun -PspringProfile=profileName` instead  
    - If you do not have access to the test database or default admin credentials, you can use an in memory database by using the command `./gradlew bootRun -PspringProfile=local` instead  

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
##### Server Profiles
The server has several profiles that can be used, allowing for the use of different databases.  
In order to use most profiles, you must have credentials set in your environment variables:  
- **local**: Uses an in memory database. For use when testing. Does not require credentials.  
- **localDev**: Uses the test database. For use when testing with persistent data, and using SSH to access the database.  
- **localProd**: Uses the prod database. This profile is not intended for future use outside of debugging the prod database.   
- **dev**: Uses the test database. For use when deploying the development server on the virtual machine.   
- **prod**: Uses the prod database. For use when deploying the production server on the virtual machine.  

### Sonarqube Analysis (Backend)
The Hakinakina program has a backend code analyser that is executed by
Sonarqube and is then published to a server for analysis. It is 
normally run on every build automatically but for manual code analysis do the 
following: To run sonarqube for the backend refer to the "Server (Backend/API)" heading
above and follow step 1, then followed by the command: './gradlew sonarqube'

##### Client (Frontend/GUI)
1. Navigate to the client folder
Example command: `cd client`
2. Install the dependencies
Command: `npm install`
3. Run the client:
Command: `npm run serve`

Running on: http://localhost:9500/

### Example User
An example user has been created, so you can view the application.  
This can be accessed by logging in using the following credentials:

**Email**:  sarahjones@yahoo.com  
**Password**:  123456

### Sonarqube Analysis (Frontend)
The Hakinakina program has a frontend code analyser that is executed by
Sonarqube and is then published to a server for analysis. It is 
normally run on every build automatically but for manual code analysis do the 
following: To run sonarqube for the front end refer to the "Client (Frontend/GUI)" heading
above and follow steps 1-2, after step 2 is complete type the
Command: 'npm run sonarqube'

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
