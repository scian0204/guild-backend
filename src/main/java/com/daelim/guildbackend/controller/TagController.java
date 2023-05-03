package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.entity.Tag;
import com.daelim.guildbackend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "태그 API")
@RestController
@RequestMapping("/api/tag")
public class TagController {
    @Autowired
    TagService tagService;

    @GetMapping("/{tagName}")
    public List<Tag> getListbyTagName(@PathVariable String tagName) {
        return tagService.getListbyTagName(tagName);
    }

    @GetMapping("/rank")
    public List<Map<String, Object>> getTagsByRank() {
        return tagService.getTagsByRank();
    }
}
