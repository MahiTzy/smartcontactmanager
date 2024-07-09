package com.scm.smartcontactmanager.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.scm.smartcontactmanager.dao.UserRepo;
import com.scm.smartcontactmanager.entities.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepo userRepo;

    Logger logger = LoggerFactory.getLogger(SuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        logger.info("User logged in successfully");

        var oauthTOken = (OAuth2AuthenticationToken) authentication;
        String oauthRegisteredId = oauthTOken.getAuthorizedClientRegistrationId();
        logger.info("OAuth Registered Id: " + oauthRegisteredId);

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        oauthUser.getAttributes().forEach((key, value) -> {
            logger.info("Key: " + key + " Value: " + value);
        });

        User user = new User();
        user.setName(oauthUser.getAttribute("name"));
        user.setRole("ROLE_USER");
        user.setProviderId(oauthUser.getName());
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode("1234"));
        user.setAbout("I am a new");

        if (oauthRegisteredId.equalsIgnoreCase("google")) {
            // Google
            user.setProvider(User.Providers.GOOGLE);
            user.setImageUrl(oauthUser.getAttribute("picture"));
            user.setEmail(oauthUser.getAttribute("email"));
            user.setName(oauthUser.getAttribute("name"));
            user.setProviderId(oauthUser.getName());

            logger.info("Google User");
        } else if (oauthRegisteredId.equalsIgnoreCase("github")) {
            // Github
            user.setProvider(User.Providers.GITHUB);
            if (oauthUser.getAttribute("email") == null) {
                user.setEmail(oauthUser.getAttribute("login").toString() + "@gmail.com");
            } else {
                user.setEmail(oauthUser.getAttribute("email"));
            }
            user.setName(oauthUser.getAttribute("name"));
            user.setImageUrl(oauthUser.getAttribute("avatar_url"));
            user.setProviderId(oauthUser.getName());
            logger.info("Github User");
        }

        User userFromDb = userRepo.getUserByUserName(user.getEmail());
        if (userFromDb == null) {
            userRepo.save(user);
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/index");
    }

}
