package com.daelim.guildbackend.controller.requestObject;

import lombok.Data;

@Data
public class BoardDeleteRequest {
    Integer boardId;
    String userId;
}
