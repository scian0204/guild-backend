package com.daelim.guildbackend.dto.response;

import com.daelim.guildbackend.entity.Board;
import com.daelim.guildbackend.entity.Party;
import com.daelim.guildbackend.entity.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class BoardResponse {
    private Board board;
    private Party party;
    private List<Tag> tags;
}
