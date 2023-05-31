package com.daelim.guildbackend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BoardUpdateRequest {
    Integer boardId;
    String userId;
    String title;
    String content;
    List<Integer> tagId;
    List<String> tagName;
    Integer total;
}
