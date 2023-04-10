package com.daelim.guildbackend.service;

import com.daelim.guildbackend.entity.Board;
import com.daelim.guildbackend.repository.BoardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Service
public class BoardService{
    ObjectMapper objMpr = new ObjectMapper();
    @Autowired()
    BoardRepository boardRepository;

    public void write(Map<String, Object> boardObj) { //글 작성 처리
        Board board = objMpr.convertValue(boardObj, Board.class);

        boardRepository.save(board);
    }

    public Page<Board> getAllBoards(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Board viewBoard(Integer idx) { //게시물 상세 페이지
        return boardRepository.findById(idx).get();
    }

    public void updateBoard(Map<String, Object> boardObj, HttpSession session) {
        Board board = objMpr.convertValue(boardObj, Board.class);
        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(board.getUserId())) {

            Optional<Board> optBoard = boardRepository.findById(board.getBoardId());
            Board board1 = optBoard.get();
            board.setWriteDate(board1.getWriteDate());

            boardRepository.save(board);
        }
    }

    public String deleteBoardPost(Map<String, Object> boardObj, HttpSession session) { //게시물 삭제
        Integer idx = Integer.parseInt((String) boardObj.get("idx"));
        String userId = (String) boardObj.get("userId");

        System.out.println("test");
        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(userId)) {
            boardRepository.deleteById(idx);
            return "0"; //userId 동일 = 삭제됨
        } else {
            return "1"; //
        }
    }
}
