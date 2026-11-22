# Etapa 1: compila el proyecto con Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# Etapa 2: imagen liviana solo con el JRE y el jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar

# El wallet no va dentro de la imagen: se monta como volumen en /app/wallet.
# DB_URL, DB_USER, DB_PASSWORD y JWT_SECRET se entregan como variables de entorno.
ENV TNS_ADMIN=/app/wallet
VOLUME /app/wallet
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
