package com.nishchay.productservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Async
    public void uploadImage(MultipartFile multipartFile, String publicId) {
        try {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());

            BufferedImage resizedImage = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, 800, 800);
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);
            byte[] resizedBytes=outputStream.toByteArray();

            Map uploadResult = cloudinary.uploader().upload(resizedBytes, ObjectUtils.asMap(
                    "public_id", publicId,
                    "quality", "auto:good"
            ));

            String url=cloudinary.url().generate(uploadResult.get("public_id").toString());
            log.info("Image uploaded successfully to Cloudinary. URL: {}", url);



        } catch (IOException e) {
            log.error("IO Exception while uploading image to Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Failed to upload image due to IO error", e);
        }catch (Exception e) {
            log.error("Error uploading image to Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}
