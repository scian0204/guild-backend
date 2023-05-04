package com.daelim.guildbackend.service;

import com.daelim.guildbackend.controller.responseObject.TagRankResponse;
import com.daelim.guildbackend.entity.Tag;
import com.daelim.guildbackend.repository.TagRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagService {
    ObjectMapper objMpr = new ObjectMapper();
    @Autowired
    TagRepository tagRepository;

    public List<Tag> getListbyTagName(String tagName) {
        return tagRepository.findByTagNameLikeIgnoreCase("%"+tagName+"%");
    }

    public List<TagRankResponse> getTagsByRank() {
        List<TagRankResponse> results = new ArrayList<>();
        List<Object> objs = tagRepository.getTagsByRank();

        objs.forEach((obj)->{
            TagRankResponse result = new TagRankResponse();
            List<Object> objList = objMpr.convertValue(obj, List.class);
            Integer cnt = Integer.parseInt(objList.get(2).toString());
            Tag tag = new Tag();
            tag.setTagId(Integer.parseInt(objList.get(0).toString()));
            tag.setTagName(objList.get(1).toString());
            result.setTag(tag);
            result.setCnt(cnt);
            results.add(result);
        });

        return results;
    }
}
