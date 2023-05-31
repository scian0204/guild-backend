package com.daelim.guildbackend.dto.response;

import com.daelim.guildbackend.entity.User;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserInfoResponse {
    private String userId;
    private String userName;
    private Timestamp regDate;

    public UserInfoResponse(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.regDate = user.getRegDate();
    }
}
