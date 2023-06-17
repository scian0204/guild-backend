package com.daelim.guildbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Data
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Entity
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer partyId;
    private Integer total;
    private Integer current;
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isActive;

    public Party(Party party) {
        this.partyId = party.getPartyId();
        this.total = party.getTotal();
        this.current = party.getCurrent();
        this.isActive = party.getIsActive();
    }
}
