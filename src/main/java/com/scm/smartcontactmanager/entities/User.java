package com.scm.smartcontactmanager.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    public enum Providers {
        SELF,
        OTHER,
        GOOGLE,
        FACEBOOK,
        GITHUB
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 20, message = "Min 2 and Max 20 characters are allowed")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "Email is mandatory")
    @Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Enter valid email")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 2, message = "Min 2 characters are allowed")
    private String password;

    private String role;

    private boolean enabled;

    private String imageUrl;

    @Enumerated(value = EnumType.STRING)
    private Providers provider = Providers.SELF;

    private String providerId;

    @Column(length = 500)
    @NotBlank(message = "About field is mandatory")
    private String about;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user", orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<Contact>();
}
