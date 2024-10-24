// SchedulerConfig.java
// Indico el paquete al que pertenece esta clase
package com.example.gestion_curriculums0.config;

// Esta clase configura el scheduler para ejecutar tareas programadas en mi aplicación

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

// Con la anotación @Configuration, indico que esta clase es una configuración para la parte de tareas programadas
@Configuration
public class SchedulerConfig {

    // Configuro un TaskScheduler para manejar las tareas programadas con un pool de hilos
    @Bean
    public TaskScheduler taskScheduler() {
        // Aquí defino un pool de 10 hilos para manejar tareas concurrentes
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);  // Defino el tamaño del pool según mis necesidades
        scheduler.setThreadNamePrefix("task-scheduler-");  // Asigno un prefijo a los nombres de los hilos para identificarlos fácilmente
        return scheduler;  // Devuelvo el scheduler configurado
    }
}
