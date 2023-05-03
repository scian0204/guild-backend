package com.daelim.guildbackend.controller.responseObject;

import com.daelim.guildbackend.entity.Board;
import com.daelim.guildbackend.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class BoardListResponse {
    Board board;
    List <com.daelim.guildbackend.entity.Tag> tags;
}
