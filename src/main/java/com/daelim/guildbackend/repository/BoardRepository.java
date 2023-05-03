package com.daelim.guildbackend.repository;

import com.daelim.guildbackend.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    List<Board> findByTitleLikeIgnoreCaseOrContentLikeIgnoreCase(Pageable pageable, String title, String content);

    Page<Board> findByUserId(Pageable pageable, String userId);

    Board findByPartyId(Integer partyId);
}
