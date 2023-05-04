package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.controller.requestObject.BoardDeleteRequest;
import com.daelim.guildbackend.controller.requestObject.BoardUpdateRequest;
import com.daelim.guildbackend.controller.requestObject.BoardWriteRequest;
import com.daelim.guildbackend.controller.responseObject.BoardListResponse;
import com.daelim.guildbackend.controller.responseObject.BoardResponse;
import com.daelim.guildbackend.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Tag(name = "Board", description = "게시판 API")
@RestController
@RequestMapping("/api/boards")
public class BoardController {
    ObjectMapper objMpr = new ObjectMapper();
    @Autowired
    BoardService boardService;

    @Operation(summary = "게시글 작성")
    @ApiResponse(description = "등록된 게시글의 boardId")
    @PostMapping("/write")
    public Integer insertBoard(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = {
                            @Content(schema = @Schema(implementation = BoardWriteRequest.class))
                    },
                    description = "tagName의 경우 같은 이름의 태그 존재 시 기존 태그 ID가 등록되며, 새로운 태그면 새로 태그 생성 후 해당 ID 등록"
            )
            @RequestBody Map<String,Object> boardObj) {
        return boardService.write(boardObj);
    }

    // 글 목록 - 내림차순
    @Operation(summary = "게시글 목록 요청 - 내림차순")
    @GetMapping("/list")
    public List<BoardListResponse> getAllBoardsDESC(@PageableDefault(page = 0, size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {
        return boardService.getAllBoards(pageable);
    }

    // 글 목록 - 오름차순
    @Operation(summary = "게시글 목록 요청 - 오름차순")
    @GetMapping("/list/ASC")
    public List<BoardListResponse> getAllBoardsASC(@PageableDefault(page = 0, size = 10, sort = "boardId", direction = Sort.Direction.ASC) Pageable pageable) {
        return boardService.getAllBoards(pageable);
    }

    // 글 목록 - 유저별
    @GetMapping("/list/{userId}")
    @Operation(summary = "유저별 게시글 목록 요청 - 오름차순")
    public List<BoardListResponse> getBoardsByUserId(@PageableDefault(page=0, size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable String userId) {
        return boardService.getBoardsByUserId(pageable, userId);
    }

    // 글 상세
    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 상세 요청")
    @ApiResponse(content = @Content(schema = @Schema(implementation = BoardResponse.class)))
    public Map<String, Object> viewBoard(@PathVariable int boardId) {
        return boardService.viewBoard(boardId);
    }

    // 글 수정
    @Operation(summary = "게시글 수정")
    @PutMapping("/update")
    public void updateBoard(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = {
                            @Content(schema = @Schema(implementation = BoardUpdateRequest.class))
                    }
            )
            @RequestBody Map<String,Object> boardObj, HttpSession session) {
        boardService.updateBoard(boardObj, session);
    }

    // 글 삭제
    @Operation(summary = "게시글 삭제")
    @ApiResponse(description = "삭제 성공 : 0<br>userId 일치하지 않음 : 1")
    @PostMapping("/delete")
    public String deleteBoardPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = BoardDeleteRequest.class))
            )
            @RequestBody Map<String,Object> boardObj, HttpSession session) {
        return boardService.deleteBoardPost(boardObj, session);
    }

    // 글 검색
    @Operation(summary = "제목 및 내용으로 게시글 검색 - 오름차순")
    @GetMapping("/search/{text}")
    public List<BoardListResponse> searchBoard(@PageableDefault(page=0, size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable String text) {
        return boardService.searchBoard(pageable, text);
    }

    @Operation(summary = "태그id로 게시글 검색 - 오름차순")
    @GetMapping("/searchByTagId/{tagId}")
    public List<BoardListResponse> searchBoardByTagId(@PageableDefault(page=0, size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Integer tagId) {
        return boardService.searchBoardByTagId(pageable, tagId);
    }
}
