package com.daelim.guildbackend.controller.requestObject;

import lombok.Data;

@Data
public class LoginRequest {
    String userId;
    String password;
}
