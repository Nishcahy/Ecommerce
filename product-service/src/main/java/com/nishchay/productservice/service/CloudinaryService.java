package com.nishchay.productservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Async
    public CompletableFuture<String> uploadImage(byte[] fileBytes, String publicId) {

        try {
            log.info("Starting upload to Cloudinary for publicId: {}", publicId);

            if (fileBytes == null || fileBytes.length == 0) {
                throw new RuntimeException("Empty file");
            }

            log.info("File size: {} bytes", fileBytes.length);

            Map result = cloudinary.uploader().upload(
                    fileBytes,
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "quality", "auto:good"
                    )
            );

            String url = result.get("secure_url").toString();
            log.info("Upload successful, URL: {}", url);

            return CompletableFuture.completedFuture(url);

        } catch (Exception e) {
            log.error("Cloudinary upload failed for publicId: {}", publicId, e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}