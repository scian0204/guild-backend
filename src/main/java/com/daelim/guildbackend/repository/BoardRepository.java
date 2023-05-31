package com.daelim.guildbackend.repository;

import com.daelim.guildbackend.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findByTitleLikeIgnoreCaseOrContentLikeIgnoreCase(Pageable pageable, String title, String content);

    Page<Board> findByUserId(Pageable pageable, String userId);

    Board findByPartyId(Integer partyId);

    @Query(value = "select b.* from Board b, TagBoard t where t.boardId = b.boardId and t.tagId = :tagId"
            , countQuery = "select * from TagBoard where tagId = :tagId"
            , nativeQuery = true
    )
    Page<Board> findByTagId(Pageable pageable, Integer tagId);

    @Query(value = "select b.* from Board b, PartyUser pu where pu.partyId = b.partyId and pu.userId = :userId"
            , countQuery = "select p.* from PartyUser p where p.userId = :userId"
            , nativeQuery = true
    )
    Page<Board> findPartyByUserId(Pageable pageable, @Param(value = "userId") String userId);
}
