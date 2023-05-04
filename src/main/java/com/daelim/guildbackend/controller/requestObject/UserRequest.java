package com.daelim.guildbackend.controller.requestObject;

import lombok.Data;

@Data
public class UserRequest {
    String userId;
    String userName;
    String password;
}
