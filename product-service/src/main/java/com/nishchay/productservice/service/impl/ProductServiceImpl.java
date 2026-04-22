package com.nishchay.productservice.service.impl;

import com.nishchay.productservice.dto.product.CreateProductRequestDto;
import com.nishchay.productservice.dto.product.ProductCacheDto;
import com.nishchay.productservice.dto.product.ProductResponseDto;
import com.nishchay.productservice.dto.product.UpdateProductRequestDto;
import com.nishchay.productservice.entity.Product;
import com.nishchay.productservice.exception.ProductException;
import com.nishchay.productservice.kafka.producer.ProductProducer;
import com.nishchay.productservice.redis.ProductRedis;
import com.nishchay.productservice.repository.ProductRepository;
import com.nishchay.productservice.service.CloudinaryService;
import com.nishchay.productservice.service.ProductService;
import com.nishchay.productservice.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductProducer productProducer;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ProductRedis productDAO;
    private final CloudinaryService cloudinaryService;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    public String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    @Override
    @Transactional
    public ProductResponseDto saveProduct(CreateProductRequestDto dto) {

        try {

            MultipartFile file = dto.getMultipartFile();

            if (file == null || file.isEmpty()) {
                throw new ProductException("Image is required", HttpStatus.BAD_REQUEST);
            }

            LOGGER.info("Received file: {}, size: {}", file.getOriginalFilename(), file.getSize());

            String publicId = System.currentTimeMillis() + "_" +
                    file.getOriginalFilename();

            // ✅ convert BEFORE async
            byte[] fileBytes = file.getBytes();
            LOGGER.info("Converted to bytes, length: {}", fileBytes.length);

            CompletableFuture<String> futureUrl =
                    cloudinaryService.uploadImage(fileBytes, publicId);

            String imageUrl = futureUrl.get();
            LOGGER.info("Got image URL: {}", imageUrl);

            Product product = modelMapper.map(dto, Product.class);
            product.setId(UUID.randomUUID().toString());
            product.setImageUrl(imageUrl);

            return modelMapper.map(productRepository.save(product), ProductResponseDto.class);

        } catch (Exception e) {
            throw new ProductException("Failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    public ProductResponseDto getProductById(String id) {

        ProductCacheDto cachedProduct = productDAO.findByProductId(id);

        if (cachedProduct != null) {
            LOGGER.info("Cache hit for product id: {}", id);
            return modelMapper.map(cachedProduct, ProductResponseDto.class);
        } else {
            LOGGER.info("Cache miss for product id: {}", id);
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductException("Product not found with id: " + id, HttpStatus.NOT_FOUND));

            productDAO.save(product);

            return modelMapper.map(product, ProductResponseDto.class);
        }
    }

    @Override
    public Page<ProductResponseDto> getProductList(int page, int size) {

        Page<Product> products=productRepository.findAll(PageRequest.of(page,size));
        List<ProductResponseDto> productResponseDtos=products.getContent().stream()
                .map(product -> {
                    ProductCacheDto cachedProduct=productDAO.findByProductId(product.getId());
                    if(cachedProduct==null){
                        productDAO.save(product);
                    }
                    return modelMapper.map(product, ProductResponseDto.class);
                }).toList();
        return new PageImpl<>(productResponseDtos,PageRequest.of(page,size),products.getTotalElements());
    }

    @Override
    public ProductResponseDto updateProduct(String id, UpdateProductRequestDto productUpdateDto, int version) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    if (existingProduct.getVersion() != version) {
                        throw new ProductException("Version mismatch. Current version: " + existingProduct.getVersion(), HttpStatus.CONFLICT);
                    }
                    modelMapper.map(productUpdateDto, existingProduct);
                    Product updatedProduct = productRepository.save(existingProduct);
                    productDAO.save(updatedProduct);
                    return modelMapper.map(updatedProduct, ProductResponseDto.class);
                })
                .orElseThrow(() -> new ProductException("Product not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    public void deleteProduct(String id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with ID: " + id, HttpStatus.NOT_FOUND));

        productRepository.delete(existingProduct);
        productDAO.deleteByProductId(id);

    }

    @Override
    public List<ProductResponseDto> getProductsByIds(Set<String> productIds) {
        return productRepository.findAllByIdIn(productIds)
                .stream()
                .map(product -> modelMapper.map(product, ProductResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponseDto> searchProducts(String name, String categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification.where((Specification<Product>) null);

        if(name!=null && !name.isEmpty()){
            spec=spec.and(ProductSpecification.hasName(name));
        }
        if(categoryId !=null && !categoryId.isEmpty() ){
            spec=spec.and(ProductSpecification.inCategory(categoryId));
        }

        if(minPrice!=null && maxPrice!=null){
            spec=spec.and(ProductSpecification.hasPriceBetween(minPrice,maxPrice));
        }

        Page<Product> products=productRepository.findAll(spec,pageable);
        return products.map(product ->
                modelMapper.map(product, ProductResponseDto.class)

        );

    }
}
