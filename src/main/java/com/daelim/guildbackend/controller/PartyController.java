package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.service.PartyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Party", description = "파티 API")
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

    @PostMapping("/isJoin")
    public boolean isJoin(@RequestBody Map<String, Object> partyUserObj) {
        return partyService.isJoin(partyUserObj);
    }

    @GetMapping("/{userId}")
    public List<Map<String, Object>> getUserJoinParty(@PageableDefault(page=0, size = 10, sort = "partyId", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable String userId) {
        return partyService.getUserJoinParty(pageable, userId);
    }
}
