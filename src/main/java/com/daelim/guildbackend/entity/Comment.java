package com.daelim.guildbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;

@Data
@DynamicInsert
@DynamicUpdate
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cmtId;
    private Integer boardId;
    private String userId;
    private Integer target;
    private String comment;
    @Column(columnDefinition = "TINYINT(1)")
    private boolean isPublic;
    private Timestamp writeDate;
}
