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


### Todo
- Add team name into `build.gradle` and `package.json`
- Set up Gitlab CI server (refer to the student guide on learn)

### Reference
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#preface)
- [Vue docs](https://vuejs.org/v2/guide/)

