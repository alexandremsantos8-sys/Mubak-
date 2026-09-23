@echo off
call "Mubak!\mvnw.cmd" -f "Mubak!\pom.xml" -q spring-boot:run "-Dspring-boot.run.arguments=--spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect --spring.jpa.hibernate.ddl-auto=none --spring.datasource.hikari.initialization-fail-timeout=-1"

//COMANDO PARA EXECUTAR