package com.daelim.guildbackend.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    String userId;
    String password;
}
