package com.nishchay.orderservice.repository;

import com.nishchay.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PathVariable;

public interface OrderRepository extends JpaRepository<Order,String> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
}
