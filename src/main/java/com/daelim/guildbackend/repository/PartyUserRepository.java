package com.daelim.guildbackend.repository;

import com.daelim.guildbackend.entity.PartyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyUserRepository extends JpaRepository<PartyUser, Integer> {
}
