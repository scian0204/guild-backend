package com.daelim.guildbackend.repository;

import com.daelim.guildbackend.entity.PartyUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyUserRepository extends JpaRepository<PartyUser, Integer> {
    Optional<PartyUser> findByUserIdAndPartyId(String userId, Integer partyId);
    Page<PartyUser> findByUserId(Pageable pageable, String userId);
}
