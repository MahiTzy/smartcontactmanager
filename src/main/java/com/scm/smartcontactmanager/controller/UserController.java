package com.scm.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.smartcontactmanager.dao.ContactRepo;
import com.scm.smartcontactmanager.dao.UserRepo;
import com.scm.smartcontactmanager.entities.Contact;
import com.scm.smartcontactmanager.entities.User;
import com.scm.smartcontactmanager.helper.Message;
// import com.stripe.exception.StripeException;
// import com.stripe.model.PaymentIntent;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ContactRepo contactRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        // System.out.println("USERNAME: " + userName);
        User user = userRepo.getUserByUserName(userName);
        // System.out.println("USER: " + user);
        model.addAttribute("user", user);
    }

    @GetMapping("/index")
    public String userDashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "user/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model, Principal principal) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "user/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
            @RequestParam("inputImage") MultipartFile multipartFile, Model model, Principal principal,
            HttpSession session) {
        // String file = contact.getImage();
        // System.out.println("FILE: " + file);
        model.addAttribute("title", "Add Contact");
        try {
            if (!multipartFile.isEmpty()) {
                contact.setImage(multipartFile.getOriginalFilename());
                File file = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
                Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
            } else {
                contact.setImage("contact.png");
            }
            User user = userRepo.getUserByUserName(principal.getName());
            contact.setUser(user);
            user.getContacts().add(contact);
            this.userRepo.save(user);
            System.out.println("DATA: " + contact);
            model.addAttribute("contact", new Contact());
            session.setAttribute("message", new Message("Your contact is added !! Add more..", "alert-success"));
            return "user/add_contact_form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("contact", contact);
            session.setAttribute("message", new Message("Something went wrong !! Try again..", "alert-danger"));
            return "user/add_contact_form";
        }
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
        model.addAttribute("title", "Show User Contacts");
        String userName = principal.getName();
        User user = userRepo.getUserByUserName(userName);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepo.findContactByUser(user.getId(), pageable);
        // model.addAttribute("contacts", user.getContacts());
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "user/show_contacts";
    }

    @GetMapping("/edit-contact/{cId}")
    public String editContact(@PathVariable("cId") int id, Model model, Principal principal) {
        model.addAttribute("title", "Edit Contact");
        Contact contact = this.contactRepo.findById(id).get();
        model.addAttribute("contact", contact);
        return "user/edit_contact";
    }

    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("inputImage") MultipartFile file,
            Model model, HttpSession session, Principal principal) {
        try {
            Contact oldContact = this.contactRepo.findById(contact.getCId()).get();
            if (!file.isEmpty()) {
                // Delete old photo
                File oldFile = new ClassPathResource("static/img").getFile();
                File oldFile2 = new File(oldFile, oldContact.getImage());
                oldFile2.delete();
                // Update new photo
                oldContact.setImage(file.getOriginalFilename());
                File oldFile1 = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(oldFile1.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            oldContact.setName(contact.getName());
            oldContact.setSecondName(contact.getSecondName());
            oldContact.setEmail(contact.getEmail());
            oldContact.setPhone(contact.getPhone());
            oldContact.setWork(contact.getWork());
            oldContact.setDescription(contact.getDescription());
            this.contactRepo.save(oldContact);
            session.setAttribute("message", new Message("Contact updated successfully..", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Error updating contact: " + e.getMessage(), "alert-danger"));
        }
        System.out.println("CONTACT: " + contact);
        return "redirect:/user/show-contacts/0";
    }

    @GetMapping("/delete-contact/{cId}")
    public String deleteContact(@PathVariable("cId") int id, Model model, Principal principal, HttpSession session) {
        try {
            Contact contact = this.contactRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contact not found"));

            String imagePath = contact.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                File oldFile = new ClassPathResource("static/img").getFile();
                File oldFile2 = new File(oldFile, imagePath);
                oldFile2.delete();
            }
            User user = userRepo.getUserByUserName(principal.getName());
            user.getContacts().remove(contact);
            this.userRepo.save(user);
            // contact.setUser(null);
            // this.contactRepo.delete(contact);
            session.setAttribute("message", new Message("Contact deleted successfully..", "alert-success"));
        } catch (Exception e) {
            session.setAttribute("message", new Message("Error deleting contact: " + e.getMessage(), "alert-danger"));
        }
        return "redirect:/user/show-contacts/0";
    }

    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer id, Model model, Principal principal) {
        System.out.println("ID: " + id);
        Contact contact = this.contactRepo.findById(id).get();
        String userName = principal.getName();
        User user = userRepo.getUserByUserName(userName);
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }
        return "user/contact_detail";
    }

    @GetMapping("/profile")
    public String yourProfile(Model model) {
        model.addAttribute("title", "Profile");
        return "user/profile";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("title", "Settings");
        return "user/settings";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword, Model model, Principal principal, HttpSession session) {
        String userName = principal.getName();
        User user = userRepo.getUserByUserName(userName);

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(this.passwordEncoder.encode(newPassword));
            userRepo.save(user);
            session.setAttribute("message", new Message("Password changed successfully..", "alert-success"));
        } else {
            session.setAttribute("message", new Message("Old password is incorrect..", "alert-danger"));
        }
        return "redirect:/user/settings";
    }

}
