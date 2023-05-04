package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.controller.requestObject.PartyUserRequest;
import com.daelim.guildbackend.controller.responseObject.BoardListResponse;
import com.daelim.guildbackend.service.PartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "파티 가입")
    @ApiResponse(description = "가입성공 : 가입한 파티ID<br>가입실패 : null")
    @PostMapping("/join")
    public Integer joinParty (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = PartyUserRequest.class)
                    )
            )
            @RequestBody Map<String, Object> partyUserObj) {
        return partyService.joinParty(partyUserObj);
    }

    @Operation(summary = "파티 탈퇴")
    @PostMapping("/leave")
    public void leaveParty(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = PartyUserRequest.class)
                    )
            )
            @RequestBody Map<String, Object> partyUserObj) {
        partyService.leaveParty(partyUserObj);
    }

//    @Operation(summary = "파티 가입 여부", description = "해당 파티에 해당 유저가 가입되어 있는지 확인")
//    @PostMapping("/isJoin")
//    public boolean isJoin(
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    content = @Content(
//                            schema = @Schema(implementation = PartyUserRequest.class)
//                    ),
//                    description = ""
//            )
//            @RequestBody Map<String, Object> partyUserObj) {
//        return partyService.isJoin(partyUserObj);
//    }

    @Operation(summary = "유저별 가입한 파티 목록 - 오름차순")
    @GetMapping("/{userId}")
    public List<BoardListResponse> getUserJoinParty(@PageableDefault(page=0, size = 10, sort = "partyId", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable String userId) {
        return partyService.getUserJoinParty(pageable, userId);
    }
}
