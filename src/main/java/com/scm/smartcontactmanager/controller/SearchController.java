package com.scm.smartcontactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.scm.smartcontactmanager.dao.ContactRepo;
import com.scm.smartcontactmanager.dao.UserRepo;
import com.scm.smartcontactmanager.entities.Contact;
import com.scm.smartcontactmanager.entities.User;

@RestController
public class SearchController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ContactRepo contactRepo;

    private User getUserFromPrincipal(Principal principal, Authentication authentication) {
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
            String email = oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email")
                    : oauthUser.getAttribute("login").toString() + "@gmail.com";
            return userRepo.getUserByUserName(email);
        } else {
            String email = principal.getName();
            return userRepo.getUserByUserName(email);
        }
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal, Authentication authentication) {
        User user = getUserFromPrincipal(principal, authentication);
        List<Contact> contacts = null;
        if (query.matches("^[0-9]*$")) {
            contacts = contactRepo.findByPhoneAndUserContaining("%" + query + "%", user);
        } else {
            contacts = contactRepo.findByNameAndUserContaining("%" + query + "%", user);
        }
        return ResponseEntity.ok(contacts);
    }
    
}
