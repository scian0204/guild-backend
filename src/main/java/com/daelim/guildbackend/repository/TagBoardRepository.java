package com.daelim.guildbackend.repository;

import com.daelim.guildbackend.entity.TagBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagBoardRepository extends JpaRepository<TagBoard, Integer> {
    List<TagBoard> findByBoardId(Integer boardId);
    void deleteAllByBoardId(Integer boardId);
}
