package com.nishchay.productservice.repository;

import com.nishchay.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, String> , JpaSpecificationExecutor<Product> {

    List<Product> findAllByIdIn(Set<String> productId);
    @Override
    Page<Product> findAll(Pageable pageable);
}
