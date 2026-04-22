package com.nishchay.productservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nishchay.commonlib.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends AbstractEntity {

    @Id
    private String id;

    private String name;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Category> children;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products;

}
