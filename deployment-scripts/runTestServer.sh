fuser -k 9499/tcp || true
source test-server/vars.config
java -jar test-server/libs/server-0.0.1-SNAPSHOT.jar
\ --server.port=9499
\ --spring.jpa.hibernate.ddl-auto='update'
\ --spring.datasource.url='jdbc:mariadb://db2.csse.canterbury.ac.nz:3306/seng302-2020-team700-test'
\ --spring.datasource.driver-class-name='org.mariadb.jdbc.Driver'
\ --spring.datasource.username=$DB_USER
\ --spring.datasource.password=$DB_PASSWORD
\ --spring.jpa.properties.hibernate.search.default.directory_provider='local-heap'
\ --ADMIN_EMAIL='admin@edmun.com'
\ --ADMIN_PASSWORD='Password1'
\ --spring.mail.host='smtp.gmail.com'
\ --spring.mail.port=587
\ --spring.mail.username='edmungoat2020'
\ --spring.mail.password='EdmunGoat2020'
\ --spring.mail.properties.mail.smtp.starttls.enable='true'
\ --spring.mail.properties.mail.smtp.starttls.required='true'
\ --spring.mail.properties.mail.smtp.auth='true'
\ --spring.mail.properties.mail.smtp.connectiontimeout=5000
\ --spring.mail.properties.mail.smtp.timeout=5000
\ --spring.mail.properties.mail.smtp.writetimeout=5000
