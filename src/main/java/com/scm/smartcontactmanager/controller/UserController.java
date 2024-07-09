package com.scm.smartcontactmanager.controller;

import java.io.File;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import com.scm.smartcontactmanager.forms.ContactForm;
import com.scm.smartcontactmanager.helper.Message;
import com.scm.smartcontactmanager.services.ImageService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ContactRepo contactRepo;
    @Autowired
    private ImageService imageService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal, Authentication authentication) {
        User user = null;
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
            String email = oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email")
                    : oauthUser.getAttribute("login").toString() + "@gmail.com";
            user = userRepo.getUserByUserName(email);

        } else {
            String email = principal.getName();
            user = userRepo.getUserByUserName(email);
        }
        model.addAttribute("user", user);
    }

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

    @GetMapping("/index")
    public String userDashboard(Model model) {
        model.addAttribute("title", "User Dashboard");
        return "user/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contactForm", new ContactForm());
        return "user/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute @Valid ContactForm contactForm,
            BindingResult result,
            Model model, Principal principal,
            Authentication authentication,
            HttpSession session) {
        model.addAttribute("title", "Add Contact");

        if (result.hasErrors()) {
            model.addAttribute("contactForm", contactForm);
            session.setAttribute("message", new Message("Validation failed !!", "alert-danger"));
            return "user/add_contact_form";
        }

        try {
            User user = getUserFromPrincipal(principal, authentication);
            Contact contact = new Contact();
            contact.setName(contactForm.getName());
            contact.setSecondName(contactForm.getSecondName());
            contact.setWork(contactForm.getWork());
            contact.setEmail(contactForm.getEmail());
            contact.setPhone(contactForm.getPhone());
            contact.setDescription(contactForm.getDescription());
            contact.setFavourite(false);
            contact.setUser(user);

            if (contactForm.getImage() != null && !contactForm.getImage().isEmpty()) {
                MultipartFile multipartFile = contactForm.getImage();
                String fileName = UUID.randomUUID().toString();
                String fileUrl = imageService.uploadImage(multipartFile, fileName);
                contact.setImageUrl(fileUrl);
                contact.setPublicId(fileName);
            }

            user.getContacts().add(contact);
            this.userRepo.save(user);

            model.addAttribute("contactForm", new ContactForm());
            session.setAttribute("message", new Message("Your contact is added !! Add more..", "alert-success"));
            return "user/add_contact_form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("contactForm", contactForm);
            session.setAttribute("message", new Message("Something went wrong !! Try again..", "alert-danger"));
            return "user/add_contact_form";
        }
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal,
            Authentication authentication) {

        model.addAttribute("title", "Show User Contacts");

        // Determine the user based on authentication type
        User user = getUserFromPrincipal(principal, authentication);

        // Fetch contacts for the user
        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepo.findContactByUser(user.getId(), pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("totalPages", contacts.getTotalPages());
        model.addAttribute("currentPage", page);

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
                String fileName = UUID.randomUUID().toString();
                String fileUrl = imageService.uploadImage(file, fileName);
                oldContact.setImageUrl(fileUrl);
                oldContact.setPublicId(fileName);
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
    public String deleteContact(@PathVariable("cId") int id, Model model, Principal principal,
            Authentication authentication, HttpSession session) {
        try {
            Contact contact = this.contactRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contact not found"));

            String imagePath = contact.getImageUrl();
            if (imagePath != null && !imagePath.isEmpty()) {
                File oldFile = new ClassPathResource("static/img").getFile();
                File oldFile2 = new File(oldFile, imagePath);
                oldFile2.delete();
            }
            User user = getUserFromPrincipal(principal, authentication);
            user.getContacts().remove(contact);
            this.userRepo.save(user);
            session.setAttribute("message", new Message("Contact deleted successfully..", "alert-success"));
        } catch (Exception e) {
            session.setAttribute("message", new Message("Error deleting contact: " + e.getMessage(), "alert-danger"));
        }
        return "redirect:/user/show-contacts/0";
    }

    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer id, Model model, Principal principal,
            Authentication authenticaiotn) {
        System.out.println("ID: " + id);
        Contact contact = this.contactRepo.findById(id).get();
        User user = getUserFromPrincipal(principal, authenticaiotn);
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
            @RequestParam("newPassword") String newPassword, Model model, Principal principal, HttpSession session,
            Authentication authentication) {

        User user = getUserFromPrincipal(principal, authentication);

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
