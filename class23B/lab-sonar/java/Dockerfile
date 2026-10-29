# Biblioteca API em Java (Aula 23B).
# Build em dois estagios: o Maven compila dentro do container (nao precisa de
# JDK na maquina para subir o ambiente) e a imagem final leva so' o JRE.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src/ src/
RUN mvn -q package -DskipTests -Djacoco.skip=true

FROM eclipse-temurin:17-jre
WORKDIR /srv
COPY --from=build /build/target/biblioteca-api.jar app.jar
RUN useradd --create-home biblioteca
USER biblioteca
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
