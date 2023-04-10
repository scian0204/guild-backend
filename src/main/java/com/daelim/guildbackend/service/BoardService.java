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

    public void write(Map<String, Object> boardObj, MultipartFile file) throws Exception { //글 작성 처리
        Board board = objMpr.convertValue(boardObj, Board.class);
//        String idx = (String) boardObj.get("idx");
        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\files"; //저장할 경로 지정

        if (file != null) {
            File saveFile = new File(filePath, boardObj.get("idx").toString());
            file.transferTo(saveFile);
            board.setImageLoc(String.valueOf(saveFile));
        }

        boardRepository.save(board);
    }

    public Page<Board> getAllBoards(Pageable pageable) { //게시글 리스트 처리
//        long boardsCount = boardRepository.count(); //게시글 수 0개 조건문??
        return boardRepository.findAll(pageable);
    }

    public Board viewBoard(Integer idx) { //게시물 상세 페이지
        return boardRepository.findById(idx).get();
    }

    public void updateBoard(Map<String, Object> boardObj, HttpSession session) throws Exception { //게시물 수정
//        System.out.println("test1");
        Board board = objMpr.convertValue(boardObj, Board.class);
//        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\files"; //이미지 수정용 주소 (필요한가??)

//        session.setAttribute("userId", "test"); //테스트용
//        System.out.println(session.getAttribute("userId") != null && session.getAttribute("userId").equals(board.getUserId()));
        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(board.getUserId())) {
//            System.out.println("test2");

//            board.setTitle(board.getTitle());
//            board.setContent(board.getContent()); //?

            Optional<Board> optBoard = boardRepository.findById(board.getIdx());
            Board board1 = optBoard.get();
            board.setWriteDate(board1.getWriteDate());
            board.setImageLoc(board1.getImageLoc()); // 날짜와 파일경로 null값으로 넘길 경우 db도 null 수정됨을 방지

//            File saveFile = new File(filePath, (String) boardObj.get("idx"));
//            file.transferTo(saveFile);
//            board.setImageLoc(String.valueOf(saveFile));

            boardRepository.save(board);
        }
    }

/*    public void deleteBoard(Integer idx, HttpSession session) { //게시물 삭제
        //userId가 접속된 userId와 동일한지 비교??,,,,
//        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(board.getUserId())) {
//        }
        boardRepository.deleteById(idx);
    }*/

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
