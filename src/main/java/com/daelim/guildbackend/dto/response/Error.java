package com.daelim.guildbackend.dto.response;

import lombok.Data;

@Data
public class Error {
    private Integer errorId;
    private String message;
}
