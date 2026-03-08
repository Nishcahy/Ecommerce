package com.nishchay.identity_service.entity;

import com.nishchay.identity_service.enums.ERole;
import jakarta.persistence.*;

import lombok.Getter;

import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    @Enumerated(EnumType.STRING)
    private ERole role;

}
