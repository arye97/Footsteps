# SENG302 Team 600 Hakinakina Application
-----
This is the README file for the SENG302 Team 600 Hakinakina Application.

Hakinakina is a fitness and leisure tracking web application.
It has been built using javascript - utilising Vue - and java - using Spring.

### Basic Project Structure
- client/src Frontend source code (JS - Vue)
- client/public publicly accesable web assets
- client/dist Frontend production build

- server/src Backend source code (Java - Spring)
- server/out Backend production build

### How to run
To run the full application, first run the server and then run the client
##### Server (Backend/API)
1. Navigate to the server folder: 
Example command: `cd server`
2. Run the server
Command: `./gradlew bootRun`

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
- Editing users with a POST by sending the id of the user to edit, and all atributes that are edited
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

### Third Party Dependencies
The Hakinakina program does use some external libraries. These are listed below.

**Given dependencies**:  
These were approved by the teaching staff before the project commenced

**Further approved dependencies**:

Backend:
- Spring Security
- MariaDB

Frontend:
- Bootstrap-Vue
- Vue-router
- Vue-multiselect
- Vue-jest
- @vue/test-utils
