package com.scm.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scm.smartcontactmanager.entities.User;


public interface UserRepo extends JpaRepository<User, Integer>{
    @Query("select u from User u where u.email = :email")
    public User getUserByUserName(@Param("email") String email);

    // @Query("select u from User u where u.name = :name")
    // public User getUserByOAuthUserName(String name);
}
