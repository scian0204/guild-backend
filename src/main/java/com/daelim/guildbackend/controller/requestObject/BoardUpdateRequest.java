package com.daelim.guildbackend.controller.requestObject;

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
