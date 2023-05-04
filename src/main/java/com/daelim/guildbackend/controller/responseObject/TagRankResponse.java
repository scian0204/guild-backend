package com.daelim.guildbackend.controller.responseObject;

import com.daelim.guildbackend.entity.Tag;
import lombok.Data;

@Data
public class TagRankResponse {
    Integer cnt;
    Tag tag;
}
