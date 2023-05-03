package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.entity.Comment;
import com.daelim.guildbackend.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    CommentService commentService;

    // 게시글별 댓글 목록
    @GetMapping("/list/{boardId}")
    public Comment viewComment(@PathVariable int boardId) {
        return commentService.viewComment(boardId);
    }

    // 댓글 작성
    @PostMapping("/insert/{boardId}")
    public Integer writeComment(@RequestBody Map<String,Object> commentObj) {
        return commentService.writeComment(commentObj);
    }

    // 댓글 삭제
    @PostMapping("/{cmtId}")
    public String deleteComment(@RequestBody Map<String,Object> commentObj, HttpSession session) throws Exception {
        return commentService.deleteComment(commentObj, session);
    }

    // 댓글 수정
    @PutMapping("/{cmtId}")
    public void updateComment(@RequestBody Map<String,Object> commentObj, HttpSession session) throws Exception {
        commentService.updateComment(commentObj, session);
    }
}
