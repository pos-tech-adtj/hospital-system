# ================= STAGE 1: Build =================
# Usa uma imagem completa do Maven e JDK 21 para compilar o projeto.
# Esta imagem é grande, mas será descartada no final.
FROM maven:3.9-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do container.
WORKDIR /app

# Copia o pom.xml primeiro para aproveitar o cache de camadas do Docker.
# Se as dependências não mudarem, esta camada não será re-executada.
COPY pom.xml .

# Baixa todas as dependências do projeto.
RUN mvn dependency:go-offline

# Copia o restante do código-fonte.
COPY src ./src

# Compila o projeto e gera o arquivo .jar. O -DskipTests acelera o build.
RUN mvn clean package -DskipTests


# ================= STAGE 2: Runtime =================
# Usa uma imagem mínima Alpine, apenas com o Java Runtime Environment (JRE).
# Alpine reduz drasticamente o tamanho da imagem final em relação a uma JDK completa.
FROM eclipse-temurin:21-jre-alpine

# Cria um grupo e um usuário de sistema dedicados para a aplicação.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Define o diretório de trabalho.
WORKDIR /app

# Copia o .jar gerado no STAGE 1 para a imagem final.
COPY --from=build /app/target/*.jar app.jar

# Define a propriedade do diretório e do arquivo para o novo usuário.
# Isso garante que o usuário 'appuser' tenha permissão para acessar os arquivos.
RUN chown -R appuser:appgroup /app

# Muda o contexto de execução para o usuário não-root.
# A partir deste ponto, todos os comandos subsequentes serão executados como 'appuser'.
USER appuser

# Expõe a porta que a aplicação Spring Boot usa (server.port=8081).
EXPOSE 8081

# Definição do ponto de entrada da aplicação.
# Executa o JAR diretamente com Java.
ENTRYPOINT ["java", "-jar", "app.jar"]
