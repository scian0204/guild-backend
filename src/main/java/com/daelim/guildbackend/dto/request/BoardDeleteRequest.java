package com.daelim.guildbackend.dto.request;

import lombok.Data;

@Data
public class BoardDeleteRequest {
    Integer boardId;
    String userId;
}
