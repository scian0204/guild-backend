package com.daelim.guildbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;

@Data
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boardId;
    private String userId;
    private String title;
    private String content;
    private Integer partyId;
    private Integer views;
    private Timestamp writeDate;

    public Board(Board board) {
        this.boardId = board.getBoardId();
        this.userId = board.getUserId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.partyId = board.getPartyId();
        this.views = board.getViews();
        this.writeDate = board.getWriteDate();
    }
}
