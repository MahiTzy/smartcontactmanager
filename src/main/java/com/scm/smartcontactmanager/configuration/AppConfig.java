package com.scm.smartcontactmanager.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class AppConfig {
    
    @Value("${cloudinary.cloud_name}")
    private static final String CLOUD_NAME= "";
    @Value("${cloudinary.api_key}")
    private static final String API_KEY= "";
    @Value("${cloudinary.api_secret}")
    private static final String API_SECRET= "";
    
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", CLOUD_NAME,
                        "api_key", API_KEY,
                        "api_secret", API_SECRET));
    }

}
