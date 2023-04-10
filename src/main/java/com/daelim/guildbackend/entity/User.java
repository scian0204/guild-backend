package com.daelim.guildbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;

@Data
@DynamicInsert
@DynamicUpdate
//@ToString
@Entity
public class User {
    @Id
    private String userId;
    private String userName;
    private String password;
    private Timestamp regDate;
}
