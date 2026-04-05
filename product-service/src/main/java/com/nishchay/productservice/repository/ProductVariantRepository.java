package com.nishchay.productservice.repository;

import com.nishchay.productservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    public List<ProductVariant> findAllByIdIn(Set<Long> variantId);
    public List<ProductVariant> findByProductId(String productId);
}
