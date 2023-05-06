package com.daelim.guildbackend.repository;

import com.daelim.guildbackend.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByBoardId(Pageable pageable, Integer boardId);
    Comment findByCmtId(Integer cmtId);
}
