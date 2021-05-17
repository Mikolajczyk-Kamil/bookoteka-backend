package com.mikolajczyk.redude.backend.domain;

import com.mikolajczyk.redude.backend.interfaces.Observable;
import com.mikolajczyk.redude.backend.interfaces.Observer;
import com.mikolajczyk.redude.backend.mail.controller.MailController;
import com.mikolajczyk.redude.backend.mail.type.MailType;
import com.mikolajczyk.redude.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WeekMailObservers implements Observable {

    private final MailController mailController;
    private final UserService userService;
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers() {
        observers = new ArrayList<>();
        List<User> usersObservers = userService.getObservers();
        observers.addAll(usersObservers);
        for (Observer observer : observers) {
            mailController.createAndSend(observer.getName(), observer.getEmail(), "Just a simple message to you...", MailType.WEEK);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
}
