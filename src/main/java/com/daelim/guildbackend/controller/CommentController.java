package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.controller.requestObject.CommentDeleteRequest;
import com.daelim.guildbackend.controller.requestObject.CommentUpdateRequest;
import com.daelim.guildbackend.controller.requestObject.CommentWriteRequest;
import com.daelim.guildbackend.entity.Comment;
import com.daelim.guildbackend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    CommentService commentService;

    // 게시글별 댓글 목록
    @Operation(summary = "게시글별 댓글 목록")
    @GetMapping("/list/{boardId}")
    @ApiResponse(description = "target은 해당 cmtId의 답글임을 의미함<br>null이라면 일반 댓글")
    public List<Comment> viewComment(@PageableDefault(page=0, size = 10, sort = "cmtId", direction = Sort.Direction.DESC)Pageable pageable, @PathVariable int boardId) {
        return commentService.viewComment(pageable, boardId);
    }

    // 댓글 작성
    @Operation(summary = "댓글 작성")
    @PostMapping("/write")
    @ApiResponse(description = "작성된 댓글의 cmtId")
    public Integer writeComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = CommentWriteRequest.class)
                    ),
                    description = "target에는 답글에 대상이 되는 cmtId를 넣거나 일반 댓글이라면 해당 항목을 제외하고 요청"
            )
            @RequestBody Map<String,Object> commentObj) {
        return commentService.writeComment(commentObj);
    }

    // 댓글 삭제
    @Operation(summary = "댓글 삭제")
    @PostMapping("/")
    @ApiResponse(description = "삭제 성공: true<br>삭제 실패: false")
    public Boolean deleteComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = CommentDeleteRequest.class)
                    )
            )
            @RequestBody Map<String,Object> commentObj, HttpSession session) throws Exception {
        return commentService.deleteComment(commentObj, session);
    }

    // 댓글 수정
    @Operation(summary = "댓글 수정")
    @PutMapping("/")
    public void updateComment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = CommentUpdateRequest.class)
                    )
            )
            @RequestBody Map<String,Object> commentObj, HttpSession session) throws Exception {
        commentService.updateComment(commentObj, session);
    }
}
