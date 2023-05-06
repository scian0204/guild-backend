package com.daelim.guildbackend.controller.requestObject;

import lombok.Data;

@Data
public class CommentUpdateRequest {
    Integer cmtId;
    Integer boardId;
    String userId;
    Integer target;
    String comment;
}
