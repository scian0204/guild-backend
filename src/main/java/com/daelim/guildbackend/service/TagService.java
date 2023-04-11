package com.daelim.guildbackend.service;

import com.daelim.guildbackend.entity.Tag;
import com.daelim.guildbackend.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    @Autowired
    TagRepository tagRepository;

    public List<Tag> getListbyTagName(String tagName) {
        return tagRepository.findByTagNameLikeIgnoreCase("%"+tagName+"%");
    }
}
