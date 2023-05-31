package com.daelim.guildbackend.service;

import com.daelim.guildbackend.dto.response.BoardListResponse;
import com.daelim.guildbackend.dto.response.Error;
import com.daelim.guildbackend.dto.response.Response;
import com.daelim.guildbackend.entity.*;
import com.daelim.guildbackend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@org.springframework.transaction.annotation.Transactional
public class PartyService {
    ObjectMapper objMpr = new ObjectMapper();
    @Autowired
    PartyRepository partyRepository;
    @Autowired
    PartyUserRepository partyUserRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    TagBoardRepository tagBoardRepository;
    @Autowired
    TagRepository tagRepository;

    public Response<Integer> joinParty(Map<String, Object> partyUserObj) {
        Response<Integer> res = new Response<>();
        PartyUser pu = objMpr.convertValue(partyUserObj, PartyUser.class);
        Optional<Party> partyOps = partyRepository.findById(pu.getPartyId());
        Integer result = null;

        if (partyOps.isPresent()) {
            Party party = partyOps.get();
            if (party.getIsActive()) {
                result = partyUserRepository.save(pu).getPartyId();
                party.setCurrent(party.getCurrent() + 1);
                if (party.getTotal() <= party.getCurrent()) {
                    party.setIsActive(false);
                }
                partyRepository.save(party);
            } else {
                Error error = new Error();
                error.setErrorId(1);
                error.setMessage("파티가 비활성화 상태임");
                res.setError(error);
            }
        } else {
            Error error = new Error();
            error.setErrorId(0);
            error.setMessage("해당 파티가 존재하지 않음");
            res.setError(error);
        }

        res.setData(result);

        return res;
    }

    public Response<Object> leaveParty(Map<String, Object> partyUserObj) {
        Response<Object> res = new Response<>();
        PartyUser pu = objMpr.convertValue(partyUserObj, PartyUser.class);

        partyUserRepository.delete(pu);

        Party party = partyRepository.findById(pu.getPartyId()).get();
        party.setCurrent(party.getCurrent()-1);
        partyRepository.save(party);

        return res;
    }

    public boolean isJoin(Map<String, Object> partyUserObj) {
        PartyUser pu = objMpr.convertValue(partyUserObj, PartyUser.class);
        if (pu.getUserId() == null) {
            Party party = partyRepository.findById(pu.getPartyId()).get();
            return party.getIsActive();
        } else {
            return !(partyUserRepository.findByUserIdAndPartyId(pu.getUserId(), pu.getPartyId()).isPresent());
        }
    }

    public Response<Page<BoardListResponse>> getUserJoinParty(Pageable pageable, String userId) {
        Response<Page<BoardListResponse>> res = new Response<>();
        Page<Board> boards = boardRepository.findPartyByUserId(pageable, userId);
        Page<BoardListResponse> results = new BoardListResponse().toDtoList(boards);
        results.forEach(result -> {
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(result.getBoard().getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.setTags(tags);
        });

        res.setData(results);

        return res;
    }
}
