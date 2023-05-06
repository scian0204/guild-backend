package com.daelim.guildbackend.controller.requestObject;

import lombok.Data;

@Data
public class CommentWriteRequest {
    Integer boardId;
    String userId;
    Integer target;
    String comment;
}
