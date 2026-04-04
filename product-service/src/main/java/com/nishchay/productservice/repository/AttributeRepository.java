package com.nishchay.productservice.repository;

import com.nishchay.productservice.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute,Long> {
    Optional<Attribute> findByName(String name);
}
