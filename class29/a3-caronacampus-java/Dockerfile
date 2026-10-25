FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

# 1) dependências em camada própria: mudar uma classe não baixa tudo de novo
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# 2) código; um teste rápido roda no build só para o Maven baixar o provider do JUnit e o JaCoCo
#    (depois disso o serviço `testes` funciona offline, com `mvn -o`)
COPY src ./src
COPY scripts ./scripts
RUN mvn -B -q verify -Dtest=ArquiteturaTest -Djacoco.haltOnFailure=false \
 && mvn -B -q dependency:copy-dependencies -DincludeScope=runtime

EXPOSE 5000
CMD ["java", "-cp", "target/classes:target/dependency/*", "br.edu.carona.App"]
