package com.daelim.guildbackend.controller.responseObject;

import com.daelim.guildbackend.entity.Board;
import com.daelim.guildbackend.entity.Party;
import com.daelim.guildbackend.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class BoardResponse {
    Board board;
    Party party;
    List<Tag> tags;
}
