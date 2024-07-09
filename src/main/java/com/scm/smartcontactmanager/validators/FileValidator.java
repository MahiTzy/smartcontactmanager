package com.scm.smartcontactmanager.validators;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileValidator implements ConstraintValidator<ValidateFile, MultipartFile>{

    private static final long MAX_SIZE = 2 * 1024 * 1024;

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        System.out.println("FileValue: " + file);
        if(file.getOriginalFilename().equals("") || file.getOriginalFilename().equals(null)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File is empty! Please select an image file.").addConstraintViolation();
            return false;
        }
        if (file.getSize() > MAX_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size should be less than 2MB").addConstraintViolation();
            return false;
        }

        return true;
    }
    
}
