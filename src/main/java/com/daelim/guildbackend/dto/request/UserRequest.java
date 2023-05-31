package com.daelim.guildbackend.dto.request;

import lombok.Data;

@Data
public class UserRequest {
    String userId;
    String userName;
    String password;
}
