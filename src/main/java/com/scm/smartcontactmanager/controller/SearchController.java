package com.scm.smartcontactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
// import org.hibernate.mapping.List;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal) {
        System.out.println(query);
        User user = userRepo.getUserByUserName(principal.getName());
        List<Contact> contacts = contactRepo.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }
    
}
