package com.scm.smartcontactmanager.forms;

import org.springframework.web.multipart.MultipartFile;

import com.scm.smartcontactmanager.validators.ValidateFile;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactForm {

    @NotBlank(message = "First Name is required")
    private String name;

    @NotBlank(message = "Last Name is required")
    private String secondName;

    @NotBlank(message = "Profession is required")
    private String work;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Address [ example@gmail.com ]")
    private String email;

    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid Phone Number")
    private String phone;

    @ValidateFile
    @Transient
    private MultipartFile image;

    private String description;
}
