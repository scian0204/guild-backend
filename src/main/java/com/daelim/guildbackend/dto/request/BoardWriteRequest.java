package com.daelim.guildbackend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BoardWriteRequest {
    String userId;
    String title;
    String content;
    Integer total;
    List<String> tagName;
}