package com.daelim.guildbackend.controller.requestObject;

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