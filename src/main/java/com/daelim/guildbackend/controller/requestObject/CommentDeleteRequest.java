package com.daelim.guildbackend.controller.requestObject;

import lombok.Data;

@Data
public class CommentDeleteRequest {
    Integer cmtId;
    String userId;
}
