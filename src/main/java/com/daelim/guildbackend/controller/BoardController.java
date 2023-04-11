package com.daelim.guildbackend.controller;

import com.daelim.guildbackend.entity.Board;
import com.daelim.guildbackend.service.BoardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    @Autowired
    BoardService boardService;

    @PostMapping("/write")
    public Integer insertBoard(@RequestBody Map<String,Object> boardObj) {
        return boardService.write(boardObj);
    }

    // 글 목록 - 내림차순
    @GetMapping("/list")
    public List<Map<String, Object>> getAllBoardsDESC(@PageableDefault(page = 0, size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable) {
        return boardService.getAllBoards(pageable);
    }

    // 글 목록 - 오름차순
    @GetMapping("/list/ASC")
    public List<Map<String, Object>> getAllBoardsASC(@PageableDefault(page = 0, size = 10, sort = "boardId", direction = Sort.Direction.ASC) Pageable pageable) {
        return boardService.getAllBoards(pageable);
    }

    // 글 상세
    @GetMapping("/{boardId}")
    public Map<String, Object> viewBoard(@PathVariable int boardId) {
        return boardService.viewBoard(boardId);
    }

    // 글 수정
    @PutMapping("/update")
    public void updateBoard(@RequestBody Map<String,Object> boardObj, HttpSession session) {
        boardService.updateBoard(boardObj, session);
    }

    // 글 삭제
    @PostMapping("/delete")
    public String deleteBoardPost(@RequestBody Map<String,Object> boardObj, HttpSession session) {
        return boardService.deleteBoardPost(boardObj, session);
    }

    // 글 검색
    @GetMapping("/search/{text}")
    public List<Map<String, Object>> searchBoard(@PathVariable String text) {
        return boardService.searchBoard(text);
    }

    @GetMapping("/searchByTagId/{tagId}")
    public List<Map<String, Object>> searchBoardByTagId(@PathVariable Integer tagId) {
        return boardService.searchBoardByTagId(tagId);
    }
}
