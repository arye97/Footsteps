package com.springvuegradle.seng302team600.listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        System.out.println("-- HttpSessionListener#sessionCreated invoked --");
        HttpSession session = sessionEvent.getSession();
        System.out.println("session id: " + session.getId());
        session.setMaxInactiveInterval(5);//in seconds
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        System.out.println("-- HttpSessionListener#sessionDestroyed invoked --");
    }
}
