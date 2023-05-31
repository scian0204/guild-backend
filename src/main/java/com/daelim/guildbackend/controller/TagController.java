package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.dto.response.Response;
import com.daelim.guildbackend.dto.response.TagRankResponse;
import com.daelim.guildbackend.entity.Tag;
import com.daelim.guildbackend.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "태그 API")
@RestController
@RequestMapping("/api/tag")
public class TagController {
    @Autowired
    TagService tagService;

    @Operation(summary = "해당 태그이름이 포함된 태그 목록")
    @GetMapping("/like/{tagName}")
    public Response<Page<Tag>> getListbyTagName(@PageableDefault(page = 0, size = 10, sort = "tagId", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable String tagName) {
        return tagService.getListbyTagName(pageable, tagName);
    }

    @Operation(summary = "해당 태그이름을 가진 태그")
    @GetMapping("/{tagName}")
    public Response<Tag> getTagByName(@PathVariable String tagName) {
        return tagService.getTagByNmae(tagName);
    }

    @Operation(summary = "가장 많이 사용된 태그 순위")
    @ApiResponse(description = "cnt - 해당 태그가 사용된 횟수")
    @GetMapping("/rank")
    public Response<List<TagRankResponse>> getTagsByRank() {
        return tagService.getTagsByRank();
    }
}
