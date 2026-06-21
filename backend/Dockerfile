# Étape 1 : Construction
FROM eclipse-temurin:17-jdk AS build

# Définir le répertoire de travail
WORKDIR /app

# Instaler Maven
RUN apt-get update && apt-get install -y maven

# Copier les fichiers du projet dans l'image
COPY . .

# Construire le projet et générer le fichier JAR
RUN mvn clean package -DskipTests

# Étape 2 : Exécution
FROM eclipse-temurin:17-jre-alpine

# Mettre à jour les packages système pour éliminer les CVEs des packages Alpine
RUN apk update && apk upgrade --no-cache

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port de l'application
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]