# SENG302 Team 600 Footsteps Application
-----
This is the README file for the SENG302 Team 600 Footsteps Application.

Footsteps is a fitness and leisure tracking web application.  
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
- SPRING_DEFAULT_ADMIN_EMAIL=default@default.com
- SPRING_DEFAULT_ADMIN_PASSWORD=pzHqSbMXAsbh2Ajd
You should have access to the Username and Password of the database (the first 2 variables) 
and they should not be included here.  If you don't have access to these values, you can still 
run the application locally (see end of step 3).  The teaching team has requested we add the username 
and password for the default admin, so we have included them.  

2. Navigate to the server folder: Example command: `cd server`  
3. 3. If you know the correct URL of the database, you can store it in the environment variable: SPRING_DATASOURCE_URL
and run the server with `./gradlew bootRun`. 
    - You may also use a specific server profile using the command `./gradlew bootRun -PspringProfile=profileName`.
See "Server Profiles" for all options."
    - If you do not have access to the test database or default admin credentials, you can use an in memory database by using the command `./gradlew bootRun -PspringProfile=local` instead  

Running on: http://localhost:9499/

### Default Admin credentials  
**Username/Email:**  default@default.com  
**Password:**  pzHqSbMXAsbh2Ajd  

##### Server Profiles
The server has several profiles that can be used, allowing for the use of different databases.  
In order to use most profiles, you must have credentials set in your environment variables:  
- **local**: Uses an in memory database. For use when testing. Does not require credentials.  
- **localDev**: Uses the test database. For use when testing with persistent data, and using SSH to access the database.  
- **localProd**: Uses the prod database. This profile is not intended for future use outside of debugging the prod database.   
- **dev**: Uses the test database. For use when deploying the development server on the virtual machine.   
- **prod**: Uses the prod database. For use when deploying the production server on the virtual machine.  

##### SSH into University Network
If you are not on the University network (localDev or localProd) you can use the Database by SSH-ing:
`ssh -L 3306:db2.csse.canterbury.ac.nz:3306 <UC-Username>@linux.cosc.canterbury.ac.nz -fN` and enter your credentials.
If you are using Windows, see our Wiki on eng-git for how to do this.
https://eng-git.canterbury.ac.nz/seng302-2020/team-600/wikis/How%20to%20configure%20database%20access%20and%20environment%20variables

### Sonarqube Analysis (Backend)
The Footsteps program has a backend code analyser that is executed by
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
**Password**:  One23456

### Sonarqube Analysis (Frontend)
The Footsteps program has a frontend code analyser that is executed by
Sonarqube and is then published to a server for analysis. It is 
normally run on every build automatically but for manual code analysis do the 
following: To run sonarqube for the front end refer to the "Client (Frontend/GUI)" heading
above and follow steps 1-2, after step 2 is complete type the
Command: 'npm run sonarqube'

### Third Party Dependencies
The Footsteps program does use some external libraries. These are listed below.

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
These dependencies have been approved for use in the Footsteps application

Backend:
- Spring Security
- MariaDB

Frontend:
- Bootstrap-Vue (and jQuery by association)
- Vue-router
- Vue-multiselect
- Vue-jest (and babel-core by association)
- @vue/test-utils
