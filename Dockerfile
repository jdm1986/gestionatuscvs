# Usar una imagen base oficial de Java
FROM openjdk:17-jdk-slim

# Crear un directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR de la aplicación en el contenedor
COPY target/gestion_curriculums0-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto que usará tu aplicación
EXPOSE 8080

# Comando para ejecutar tu aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
