# Seng302 Example Project
-----
Basic project template using Gradle, NPM, Spring Boot, Vue and Gitlab CI. Remember to set up y
our Gitlab Ci server (refer to the student guide for instructions).

### Basic Project Structure
- client/src Frontend source code (JS - Vue)
- client/public publicly accesable web assets
- client/dist Frontend production build

- server/src Backend source code (Java - Spring)
- server/out Backend production build

### How to run
##### Client (Frotnend/GUI)
`cd client`
`npm install`
`npm run serve`

Running on: http://localhost:9500/

##### Server (Backend/API)
`cd server`
`./gradlew bootRun`

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
- Listing users with a GET and the path: '/listprofile' (a switch for testing purposes)

### Todo
- Add team name into `build.gradle` and `package.json`
- Set up Gitlab CI server (refer to the student guide on learn)

### Reference
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#preface)
- [Vue docs](https://vuejs.org/v2/guide/)

