package com.daelim.guildbackend.dto.request;

import lombok.Data;

@Data
public class CommentUpdateRequest {
    Integer cmtId;
    Integer boardId;
    String userId;
    Integer target;
    String comment;
}
