package com.daelim.guildbackend.service;

import com.daelim.guildbackend.entity.Board;
import com.daelim.guildbackend.entity.Party;
import com.daelim.guildbackend.entity.Tag;
import com.daelim.guildbackend.entity.TagBoard;
import com.daelim.guildbackend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BoardService{
    ObjectMapper objMpr = new ObjectMapper();
    Gson g = new Gson();
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    PartyRepository partyRepository;
    @Autowired
    PartyUserRepository partyUserRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    TagBoardRepository tagBoardRepository;

    public Integer write(Map<String, Object> boardObj) {
        // 파티 생성
        Party party = new Party();
        party.setTotal((Integer) boardObj.get("total"));
        party.setCurrent((Integer) boardObj.get("current"));
        boardObj.remove("total");
        boardObj.remove("current");
        int partyId = partyRepository.save(party).getPartyId();

        // 태그 존재 확인
        // 기존에 있던 태그면 tagBoard에 저장
        // 기존에 없던 태그면 생성 후 tagBoard 저장
        if (boardObj.get("tagId") != null) {
            Integer[] i = g.fromJson(boardObj.get("tagId")+"", Integer[].class);
            TagBoard tb = new TagBoard();
            tb.setBoardId((Integer)boardObj.get("boardId"));
            for (Integer tagId :
                    i) {
                tb.setTagId(tagId);
                tagBoardRepository.save(tb);
            }
            boardObj.remove("tagId");
        } else if (boardObj.get("tagName") != null) {
            String[] arr = g.fromJson(boardObj.get("tagName")+"", String[].class);
            Tag tag = new Tag();
            TagBoard tb = new TagBoard();
            tb.setBoardId((Integer)boardObj.get("boardId"));
            for (String tagName :
                    arr) {
                tag.setTagName(tagName);
                tag.setTagId(tagRepository.save(tag).getTagId());
                tb.setTagId(tag.getTagId());
                tagBoardRepository.save(tb);
            }
            boardObj.remove("tagName");
        }
        
        // boardObj를 Board 객체로 변경 후 board 테이블에 저장
        Board board = objMpr.convertValue(boardObj, Board.class);
        board.setPartyId(partyId);

        return boardRepository.save(board).getBoardId();
    }

    public Page<Board> getAllBoards(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Map<String, Object> viewBoard(Integer boardId) { //게시물 상세 페이지

        // Board - boardId로 검색 후 조회수 + 1
        Board board = boardRepository.findById(boardId).get();
        if (board.getBoardId() == null) {
            return null;
        }
        Map<String, Object> resultMap = new HashMap<>();
        board.setViews(board.getViews() + 1);
        boardRepository.save(board);
        resultMap.put("Board", board);

        // Party
        Party party = partyRepository.findById(board.getPartyId()).get();
        resultMap.put("Party", party);

        // List<Tag> - baordId로 tagBoard에서 목록 가져온 후 forEach를 이용하여 tagBoard의 tagId를 하나하나 검색하여 Tag 리스트에 삽입
        List<Tag> tags = new ArrayList<>();
        List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(boardId);
        tagBoards.forEach((tagBoard)->{
            tags.add((Tag) tagRepository.findByTagId(tagBoard.getTagId()));
        });
        resultMap.put("Tags", tags);

        return resultMap;
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
        Integer boardId = (Integer) boardObj.get("boardId");
        String userId = (String) boardObj.get("userId");

        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(userId)) {
            tagBoardRepository.deleteAllByBoardId(boardId);
            boardRepository.deleteById(boardId);
            partyRepository.deleteById((Integer) boardObj.get("partyId"));
            return "0"; //userId 동일 = 삭제됨
        } else {
            return "1"; //
        }
    }

    public Map<String, Object> test(Map<String, Object> obj) {
        Map<String, Object> list = new HashMap<>();
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag());
        tags.add(new Tag());
        tags.add(new Tag());
        tags.add(new Tag());

        list.put("Board", new Board());
        list.put("Tags", tags);
        list.put("Party", new Party());

        return list;
    }
}
