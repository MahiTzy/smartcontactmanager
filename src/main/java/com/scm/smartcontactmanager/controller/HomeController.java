package com.scm.smartcontactmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.smartcontactmanager.dao.UserRepo;
import com.scm.smartcontactmanager.entities.User;
import com.scm.smartcontactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model) {
        // User user = new User();
        // user.setName("Rahul");
        // user.setEmail("ms@gmail.com");
        // userRepo.save(user);
        // System.out.println("This is home page");
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @GetMapping("/signin")
    public String login(Model model) {
        model.addAttribute("title", "Login - Smart Contact Manager");
        return "signin";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, @RequestParam("repeatpassword") String repeatpassword, Model model,
            HttpSession session) {
        try {
            if (!agreement) {
                System.out.println("You have not agreed the terms and conditions");
                throw new Exception("You have not agreed the terms and conditions");
            }
            if (result.hasErrors()) {
                System.out.println("ERROR: " + result.toString());
                model.addAttribute("user", user);
                return "signup";
            }
            if(this.userRepo.getUserByUserName(user.getEmail()) != null) {
                throw new Exception("User with this email already exists");
            }
            if(!user.getPassword().equals(repeatpassword)) {
                throw new Exception("Passwords do not match");
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User res = this.userRepo.save(user);
            System.out.println("Registration form submitted");
            System.out.println("User: " + res);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));
            return "signup";
        }
        return "signup";
    }
}
