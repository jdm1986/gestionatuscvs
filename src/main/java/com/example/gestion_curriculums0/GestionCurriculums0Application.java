package com.example.gestion_curriculums0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.gestion_curriculums0")
@EnableJpaRepositories(basePackages = "com.example.gestion_curriculums0")
@EntityScan(basePackages = "com.example.gestion_curriculums0")
@EnableAsync
@EnableScheduling
public class GestionCurriculums0Application {

    public static void main(String[] args) {
                SpringApplication.run(GestionCurriculums0Application.class, args);
    }
}

//prueba

/* Iniciar el servidor web:

git clone --mirror https://github.com/jdm1986/gestionatuscv.git

En terminal dentro de raiz frontend creada src, pegar este comando {{ python -m http.server 8000}}

python https_server.py

Pega desde la raiz c: cd cursos\java\IdeaProyect\gestion_curriculums0\gestion_curriculums0\frontend y luego el comando de arriba.

http://localhost:8000/

Abre una terminal o línea de comandos.
Navega al directorio que contiene tu archivo index.html. Puedes usar el comando cd para cambiar de directorio. Por ejemplo:


cd C:\cursos\java\IdeaProyect\gestion_curriculums0\frontend

Inicia el servidor web usando el siguiente comando:

python -m http.server 5500

Acceder a tu archivo:

Abrir navegador web y copiar http://localhost:5500. Deberías ver tu aplicación y poder realizar las solicitudes.*/

/*

Mejoras y Características Adicionales
Autenticación y Autorización:

Implementar autenticación de usuarios para que solo usuarios registrados puedan subir y buscar currículums.
Usar JWT (JSON Web Tokens) para gestionar la autenticación.
Interfaz de Usuario Mejorada:

Mejorar la interfaz de usuario con un diseño más atractivo y funcional.
Usar frameworks de frontend como React, Angular, o Vue.js para crear una interfaz más interactiva.
Paginación y Filtros:

Implementar paginación para las búsquedas de currículums para manejar grandes cantidades de datos.
Agregar filtros adicionales, como búsqueda por fecha de creación o por ubicación.
Notificaciones por Correo Electrónico:

Enviar notificaciones por correo electrónico a los usuarios cuando se suba un nuevo currículum o cuando se realicen búsquedas específicas.
Subida de Archivos Mejorada:

Permitir la subida de múltiples archivos a la vez.
Agregar validación del tipo de archivo y tamaño antes de subir.
Seguridad Adicional:

Implementar HTTPS para asegurar la comunicación entre el cliente y el servidor.
Realizar validaciones adicionales en el backend para evitar ataques como la inyección de SQL o XSS (Cross-Site Scripting).
Documentación y Pruebas:

Documentar la API usando Swagger para que sea más fácil de entender y usar.
Escribir pruebas unitarias y de integración para asegurar la calidad del código.
Despliegue en Producción:

Preparar la aplicación para su despliegue en un entorno de producción, como AWS, Heroku, o DigitalOcean.
Configurar un pipeline de CI/CD (Integración Continua/Despliegue Continuo) para automatizar el proceso de despliegue.

-------------------

Configuración de Seguridad:

La advertencia sobre la configuración de seguridad sugiere que debes cambiar las configuraciones de seguridad para la producción.
Configura un método de autenticación seguro (por ejemplo, autenticación basada en base de datos, OAuth2, etc.) en lugar de inMemoryUserDetailsManager.
Propiedades del Archivo application.properties:

Asegúrate de que todas las propiedades sensibles, como openai.api.key, no estén en el control de versiones.
Usa variables de entorno o un servicio de gestión de secretos para almacenar y gestionar estas propiedades sensibles.
Manejo de Errores y Excepciones:

Implementa un manejo de errores y excepciones adecuado para proporcionar retroalimentación clara al usuario y para registrar cualquier problema que pueda ocurrir en producción.
Pruebas:

Vuelve a habilitar las pruebas unitarias y de integración, y asegúrate de que todas pasen.
Agrega más pruebas si es necesario para cubrir casos bord y asegurar la robustez de tu aplicación.
Registro y Monitoreo:

Configura un sistema de registro y monitoreo (por ejemplo, ELK stack, Prometheus, Grafana) para mantener un ojo en el rendimiento y la salud de tu aplicación en producción.
Despliegue:

Prepara tu aplicación para el despliegue en un entorno de producción, considerando opciones como contenedores Docker, Kubernetes, o servicios de nube como AWS, Azure, o Google Cloud.
Documentación:

Mantén una buena documentación del código, especialmente para cualquier configuración especial o instrucciones para desplegar y mantener la aplicación.

 */
