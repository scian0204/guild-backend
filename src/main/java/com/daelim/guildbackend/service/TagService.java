package com.daelim.guildbackend.service;

import com.daelim.guildbackend.dto.response.Error;
import com.daelim.guildbackend.dto.response.Response;
import com.daelim.guildbackend.dto.response.TagRankResponse;
import com.daelim.guildbackend.entity.Tag;
import com.daelim.guildbackend.repository.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    ObjectMapper objMpr = new ObjectMapper();
    @Autowired
    TagRepository tagRepository;

    public Response<Page<Tag>> getListbyTagName(Pageable pageable, String tagName) {
        Response<Page<Tag>> res = new Response<>();
        res.setData(tagRepository.findByTagNameLikeIgnoreCase(pageable, "%"+tagName+"%"));
        return res;
    }

    public Response<List<TagRankResponse>> getTagsByRank() {
        Response<List<TagRankResponse>> res = new Response<>();
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

        res.setData(results);

        return res;
    }

    public Response<Tag> getTagByNmae(String tagName) {
        Response<Tag> res = new Response<>();
        Optional<Tag> tagOptional = tagRepository.findByTagName(tagName);
        if (tagOptional.isPresent()) {
            res.setData(tagOptional.get());
        } else {
            Error error = new Error();
            error.setErrorId(0);
            error.setMessage("해당 이름을 가진 태그가 없음");
            res.setError(error);
        }

        return res;
    }
}
