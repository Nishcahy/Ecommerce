package com.nishchay.commonlib.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class AbstractEntity {

    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date updatedAt;

    @Version
    protected int version;

    @PrePersist
    protected void onCreate(){
        createdAt=new Date();
        updatedAt=new Date();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt=new Date();
    }

}
