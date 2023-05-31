package com.daelim.guildbackend.dto.request;

import lombok.Data;

@Data
public class PartyUserRequest {
    Integer partyId;
    String userId;
}
