package com.springvuegradle.seng302team600.model;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.ArrayList;

@Component
public class LoggedUser implements HttpSessionBindingListener {

    private Long userId;

    private ArrayList<Long> activeUsers;

    public LoggedUser(Long userId, ArrayList<Long> activeUsers) {
        this.userId = userId;
        this.activeUsers = activeUsers;
    }

    public LoggedUser() {

    }

    /**
     * Adds userId to activeUsers if not already added.
     * @param event HttpSessionBindingEvent
     */
    @Override
    public void valueBound (HttpSessionBindingEvent event) {
        LoggedUser loggedUser = (LoggedUser) event.getValue();
        if (!activeUsers.contains(loggedUser.getUserId())) {
            activeUsers.add(loggedUser.getUserId());
        }
    }

    /**
     * Removes userId from activeUsers of not already removed.
     * @param event HttpSessionBindingEvent
     */
    @Override
    public void valueUnbound (HttpSessionBindingEvent event) {
        LoggedUser loggedUser = (LoggedUser) event.getValue();
        if (activeUsers.contains(loggedUser.getUserId())) {
            activeUsers.remove(loggedUser.getUserId());
        }
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
