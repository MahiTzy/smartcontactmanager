package com.scm.smartcontactmanager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

@Service
public class ImageService {

    @Autowired
    private Cloudinary cloudinary;


    public String uploadImage(MultipartFile file,String fileName) {

        try {
            byte[] data = new byte[file.getInputStream().available()];
            file.getInputStream().read(data);
            cloudinary.uploader().upload(data, ObjectUtils.asMap(
                "public_id", fileName
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.getUrlFromPublicId(fileName);
    }

    public String getUrlFromPublicId(String publicId) {
        return cloudinary.url().transformation(new Transformation<>().width(500).height(500).crop("fill")).generate(publicId);
    }

}
