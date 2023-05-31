package com.daelim.guildbackend.dto.request;

import lombok.Data;

@Data
public class CommentDeleteRequest {
    Integer cmtId;
    String userId;
}
