package com.daelim.guildbackend.dto.response;

import com.daelim.guildbackend.entity.Tag;
import lombok.Data;

@Data
public class TagRankResponse {
    Integer cnt;
    Tag tag;
}
