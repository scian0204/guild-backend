package com.daelim.guildbackend.dto.request;

import lombok.Data;

@Data
public class CommentWriteRequest {
    Integer boardId;
    String userId;
    Integer target;
    String comment;
}
