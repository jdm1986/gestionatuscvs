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

    public void saveUserLog(String username, String action) {
        UserLog log = new UserLog();
        log.setUsername(username);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        userLogRepository.save(log);
    }

    public List<UserLog> getAllLogs() {
        return userLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}
