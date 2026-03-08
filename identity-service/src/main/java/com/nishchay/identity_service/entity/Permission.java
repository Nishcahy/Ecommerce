package com.nishchay.identity_service.entity;

import com.nishchay.identity_service.enums.EPermission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,unique = true)
    @Enumerated(EnumType.STRING)
    private EPermission name;
}
