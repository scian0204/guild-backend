package com.daelim.guildbackend.service;

import com.daelim.guildbackend.entity.Party;
import com.daelim.guildbackend.entity.PartyUser;
import com.daelim.guildbackend.repository.PartyRepository;
import com.daelim.guildbackend.repository.PartyUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
