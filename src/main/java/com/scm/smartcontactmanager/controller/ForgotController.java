package com.scm.smartcontactmanager.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.smartcontactmanager.dao.UserRepo;
import com.scm.smartcontactmanager.entities.User;
import com.scm.smartcontactmanager.helper.Message;
import com.scm.smartcontactmanager.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/forgot")
    public String openEmailForm() {
        return "forgot_email_form";
    }

    @PostMapping("/sendotp")
    public String sendOTP(@RequestParam("email") String email,HttpSession session) {
        // System.out.println("EMAIL: " + email);

        // Generate a random OTP
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // Generates a 4-digit OTP
        // System.out.println("OTP: " + otp);
        String subject = "OTP from SCM";
        String message = "Your OTP is " + otp;
        String to = email;
        boolean confirm = this.emailService.sendSimpleMail(to, subject, message);
        if (confirm) {
            System.out.println("Mail Sent Successfully...");
            session.setAttribute("message", new Message("OTP sent successfully...", "alert-success"));
            session.setAttribute("myotp", otp);
            session.setAttribute("email", email);

        } else {
            System.out.println("Error while Sending Mail");
            session.setAttribute("message", new Message("Error while Sending Mail", "alert-danger"));
        }

        // Send OTP via email (implement your email service logic here)

        return "verify_otp";
    }

    @PostMapping("/verifyotp")
    public String verifyOTP(@RequestParam("otp1") int otp1, @RequestParam("otp2") int otp2, @RequestParam("otp3") int otp3,
            @RequestParam("otp4") int otp4, HttpSession session) {
        // System.out.println("OTP: " + otp);
        String otp = "" + otp1 + otp2 + otp3 + otp4;
        System.out.println("OTP: " + otp);
        String myotp = "" + session.getAttribute("myotp");
        String email = (String) session.getAttribute("email");
        if (otp.equals(myotp)) {
            // System.out.println("OTP verified successfully...");
            User user = this.userRepo.getUserByUserName(email);
            if (user == null) {
                // send error message
                session.setAttribute("message", new Message("User does not exist with this email...", "alert-danger"));
                return "forgot_email_form";
            }
            session.setAttribute("message", new Message("OTP verified successfully...", "alert-success"));
            return "password_change_form";
        } else {
            // System.out.println("OTP verification failed...");
            session.setAttribute("message", new Message("OTP verification failed...", "alert-danger"));
            return "verify_otp";
        }
    }
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("newPassword") String newpassword, HttpSession session) {
        String email = (String) session.getAttribute("email");
        User user = this.userRepo.getUserByUserName(email);
        user.setPassword(this.passwordEncoder.encode(newpassword));
        this.userRepo.save(user);
        session.setAttribute("message", new Message("Password changed successfully...", "alert-success"));
        return "redirect:/signin";
    }
}

