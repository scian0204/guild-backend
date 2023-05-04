package com.daelim.guildbackend.service;

import com.daelim.guildbackend.controller.responseObject.BoardListResponse;
import com.daelim.guildbackend.entity.*;
import com.daelim.guildbackend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Integer joinParty(Map<String, Object> partyUserObj) {
        PartyUser pu = objMpr.convertValue(partyUserObj, PartyUser.class);
        Party party = partyRepository.findById(pu.getPartyId()).get();
        Integer result = null;
        if (party.getIsActive()) {
            result = partyUserRepository.save(pu).getPartyId();
            party.setCurrent(party.getCurrent() + 1);
            if (party.getTotal() <= party.getCurrent()) {
                party.setIsActive(false);
            }
            partyRepository.save(party);
        }
        return result;
    }

    public void leaveParty(Map<String, Object> partyUserObj) {
        PartyUser pu = objMpr.convertValue(partyUserObj, PartyUser.class);

        partyUserRepository.delete(pu);

        Party party = partyRepository.findById(pu.getPartyId()).get();
        party.setCurrent(party.getCurrent()-1);
        partyRepository.save(party);
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

    public List<BoardListResponse> getUserJoinParty(Pageable pageable, String userId) {
        List<BoardListResponse> results = new ArrayList<>();
        Page<PartyUser> pu = partyUserRepository.findByUserId(pageable, userId);
        List<Board> boards = new ArrayList<>();
        pu.forEach(partyUser -> {
            boards.add(boardRepository.findByPartyId(partyUser.getPartyId()));
        });
        boards.forEach(board -> {
            BoardListResponse result = new BoardListResponse();
            result.setBoard(board);
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(board.getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.setTags(tags);
            results.add(result);
        });
        return results;
    }
}
