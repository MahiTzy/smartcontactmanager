package com.scm.smartcontactmanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "CONTACT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int cId;

    private String name;

    private String secondName;

    private String work;

    private String email;

    private String phone;

    private String imageUrl;

    private boolean favourite;

    private String publicId;

    @Column(length = 5000)
    private String description;
    
    @ManyToOne
    @JsonIgnore
    private User user;
}
