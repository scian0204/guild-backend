package com.daelim.guildbackend.repository;

import com.daelim.guildbackend.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Page<Tag> findByTagNameLikeIgnoreCase(Pageable pageable, String tagName);
    Optional<Tag> findByTagName(String tagName);

    @Query(value = "select t.*, COUNT(tb.boardId) as cnt from TagBoard tb, Tag t where t.tagId = tb.tagId group by tagId order by count(boardId) desc LIMIT 20", nativeQuery = true)
    List<Object> getTagsByRank();
}
