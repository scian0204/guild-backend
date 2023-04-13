package com.daelim.guildbackend.repository;

import com.daelim.guildbackend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    List<Tag> findByTagNameLikeIgnoreCase(String tagName);
    Optional<Tag> findByTagName(String tagName);
}
