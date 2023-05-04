package com.daelim.guildbackend.controller.requestObject;

import lombok.Data;

@Data
public class PartyUserRequest {
    Integer partyId;
    String userId;
}
