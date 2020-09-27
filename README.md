# Edmun SENG302
-----
Outdoor activites tracker, where users can register, sign-in, view profile and edit profile.
Custom activity types can also be created and assainged to users.


### Basic Project Structure
- client/src Frontend source code (JS - Vue)
- client/public publicly accesable web assets
- client/node_modules Assets for node plugins
- client/src/pages Location for all Vue webpages

- server/src Backend source code (Java - Spring)
- server/gradle Backend gradle source code

### How to run
##### Setup Environment Variables
`cd server/src/main/resources`
- Navigate to the application.properties file

1. ###### Database
    - Insert the database configuration variables  
    `server.port=9499`  
    `spring.jpa.hibernate.ddl-auto=update`  
    `spring.datasource.url=jdbc:mariadb://db2.csse.canterbury.ac.nz:3306/seng302-2020-team700-prod`  
    `spring.datasource.driver-class-name=org.mariadb.jdbc.Driver`  
    - Insert and fill in your database credentials  
    `spring.datasource.username=`  
    `spring.datasource.password=`  
    
2. ###### Mail Service
    - Insert Edmun's spring mail configuration variables  
    `spring.mail.host=smtp.gmail.com`  
    `spring.mail.port=587`  
    `spring.mail.username=edmungoat2020`  
    `spring.mail.password=EdmunGoat2020`  
    `spring.mail.properties.mail.smtp.starttls.enable=true`  
    `spring.mail.properties.mail.smtp.starttls.required=true`  
    `spring.mail.properties.mail.smtp.auth=true`  
    `spring.mail.properties.mail.smtp.connectiontimeout=5000`  
    `spring.mail.properties.mail.smtp.timeout=5000`  
    `spring.mail.properties.mail.smtp.writetimeout=5000`  
    `spring.mail.url = https://csse-s302g7.canterbury.ac.nz/test/#/`  
    
3. ###### Servlet session and file  
    `spring.servlet.multipart.max-file-size=20MB`  
    `spring.servlet.multipart.max-request-size=20MB`  
    `server.servlet.session.timeout=24h`  

4. ###### Default Admin  
    - Insert the following variables and fill in the default admin credentials  
    `ADMIN_EMAIL=admin@edmun.com`  
    `ADMIN_PASSWORD=Password1`
    
5. ###### Image Directory Path  
    `IMAGE.PROFILE.DIRECTORY=./src/main/resources/photos/profile/`  
    `IMAGE.ACTIVITY.DIRECTORY=./src/main/resources/photos/activity/`  

6. ###### Google API Key
    `GOOGLE_API_KEY=AIzaSyCTTv0WSUUBcRQBnRVSrrw5HV-oC-UvS18`

    
##### Client (Frotnend/GUI)
`cd client`
`npm install`
`npm run serve`

Running on: http://localhost:9500/

##### Jest Test 
`cd client`
`npm install`
`npm run test`

##### Server (Backend/API)
`cd server`
`./gradlew bootRun`

Running on: http://localhost:9499/

### Dedicated Server Instances
##### Staged test instance (dev branch)
https://csse-s302g7.canterbury.ac.nz/test/
##### Production server instance (master branch)
https://csse-s302g7.canterbury.ac.nz/prod/

### Quality Assurance
##### SonarQube

For Client:
`cd client`
`npm run test`
`npm run sonarqube`

For Server:
`cd server`
`./gradlew bootRun`
`gradle sonarqube`

To view sonarqube analysis on a browser use: https://csse-s302g7.canterbury.ac.nz/sonarqube/account/security/
- use VM credentials for login.

### Licensing/Credits
*  Country data is retrieved using [REST Countries API](https://restcountries.eu/)
*  [Google Maps API](https://cloud.google.com/maps-platform/) is used to retrieve geographical data and displaying this data using [Leaflet](https://leafletjs.com/)

### Authors
*  Callum Aubrey
*  Harry Collard
*  Yu Duan
*  Connor Macdonald
*  Martin Lopez
*  Cheng Yi Kok
*  Louis Davies
*  William Morris
