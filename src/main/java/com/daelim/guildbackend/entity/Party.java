package com.daelim.guildbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Data
@DynamicInsert
@DynamicUpdate
@Entity
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer partyId;
    private Integer total;
    private Integer current;
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isActive;
}
