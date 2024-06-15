package com.scm.smartcontactmanager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
@Service
public class SessionUtilityBean {
    private static final Logger log = LoggerFactory.getLogger(SessionUtilityBean.class); // Add this line to initialize the logger

    public void removeVerificationMessageFromSession() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession(false); // Use false to avoid creating a new session if it doesn't exist
            if (session != null) {
                session.removeAttribute("message");
            }
        } catch (RuntimeException ex) {
            log.error("No Request: ", ex);
        }
    }
}