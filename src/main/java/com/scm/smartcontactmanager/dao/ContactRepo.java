package com.scm.smartcontactmanager.dao;

import java.util.List;

// import org.hibernate.mapping.List;
// import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scm.smartcontactmanager.entities.Contact;
import com.scm.smartcontactmanager.entities.User;

public interface ContactRepo extends JpaRepository<Contact, Integer> {

    @Query("from Contact as c where c.user.id = :userId")
    public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);

    @Query("from Contact as c where (c.name LIKE :name OR c.secondName LIKE :name) AND c.user = :user")
    List<Contact> findByNameAndUserContaining(@Param("name") String name, @Param("user") User user);

    @Query("from Contact as c where (c.phone LIKE :phone) AND c.user = :user")
    List<Contact> findByPhoneAndUserContaining(String phone, User user);
}
