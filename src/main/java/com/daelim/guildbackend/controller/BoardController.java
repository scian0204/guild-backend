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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    @Autowired
    BoardService boardService;

    @PostMapping("/write")
    public void insertBoard(@RequestBody Map<String,Object> boardObj) throws Exception {
        boardService.write(boardObj); //하단 insertBoard2랑 비교 확인
    }

//    @PostMapping("/write") //return 값이 String
//    public String insertBoard2(@RequestBody Map<String,Object> boardObj, MultipartFile file) throws Exception {
//       return boardService.write2(boardObj, file);
//    }

    @GetMapping("/list")
    public Page<Board> getAllBoardsDESC(@PageableDefault(page = 0, size = 10, sort = "idx", direction = Sort.Direction.DESC) Pageable pageable) {
        return boardService.getAllBoards(pageable);
    }
    @GetMapping("/list/ASC")
    public Page<Board> getAllBoardsASC(@PageableDefault(page = 0, size = 10, sort = "idx", direction = Sort.Direction.ASC) Pageable pageable) {
        return boardService.getAllBoards(pageable);
    }

    @PutMapping("/update")
    public void updateBoard(@RequestBody Map<String,Object> boardObj, HttpSession session) throws Exception {
        boardService.updateBoard(boardObj, session);
    }

    @GetMapping("/{idx}") // <- idx가 자동으로 들어감(@PathVariable)
    public Board viewBoard(@PathVariable int idx) {
        return boardService.viewBoard(idx);
    }

    @PostMapping("/delete") //POST 형식
    public String deleteBoardPost(@RequestBody Map<String,Object> boardObj, HttpSession session) throws Exception {
        return boardService.deleteBoardPost(boardObj, session);
    }
}
