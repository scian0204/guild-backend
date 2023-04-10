package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.entity.Tag;
import com.daelim.guildbackend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {
    @Autowired
    TagService tagService;

    @GetMapping("/{tagName}")
    public List<Tag> getListbyTagName(@PathVariable String tagName) {
        return tagService.getListbyTagName(tagName);
    }
}
