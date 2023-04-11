package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.service.PartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/party")
public class PartyController {
    @Autowired
    PartyService partyService;

    @PostMapping("/join")
    public Integer joinParty (@RequestBody Map<String, Object> partyUserObj) {
        return partyService.joinParty(partyUserObj);
    }

    @PostMapping("/leave")
    public void leaveParty(@RequestBody Map<String, Object> partyUserObj) {
        partyService.leaveParty(partyUserObj);
    }
}
