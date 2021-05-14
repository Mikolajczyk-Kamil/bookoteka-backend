package com.mikolajczyk.redude.backend.log;

import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.log.domain.Log;
import com.mikolajczyk.redude.backend.log.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    public void log(Log log) {
        if (log.getUser() != null)
            log.getUser().getLogs().add(log);
        if (log.getBook() != null)
            log.getBook().getLogs().add(log);
        logService.save(log);
    }

    public void deleteAllUserLog(User user) {
        logService.deleteAllByUser(user);
    }
}
