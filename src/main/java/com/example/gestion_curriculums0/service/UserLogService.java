/* Esta clase gestiona la lógica relacionada con los registros de actividad (logs) de los usuarios.
   Permite guardar nuevas entradas de logs y consultar todos los logs registrados.*/

package com.example.gestion_curriculums0.service;

import com.example.gestion_curriculums0.model.UserLog;
import com.example.gestion_curriculums0.repository.UserLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserLogService {

    @Autowired
    private UserLogRepository userLogRepository;

    // Guardo una nueva entrada de log con el nombre de usuario y la acción realizada
    public void saveUserLog(String username, String action) {
        UserLog log = new UserLog();
        log.setUsername(username); // Establezco el nombre del usuario que realizó la acción
        log.setAction(action); // Establezco la acción realizada por el usuario
        log.setTimestamp(LocalDateTime.now()); // Establezco la hora exacta de la acción
        userLogRepository.save(log); // Guardo el log en la base de datos
    }

    // Obtengo todos los logs, ordenados de manera descendente por la marca de tiempo (timestamp)
    public List<UserLog> getAllLogs() {
        return userLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        // Retorno los logs más recientes primero
    }
}
