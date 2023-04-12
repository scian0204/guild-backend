package com.daelim.guildbackend.service;

import com.daelim.guildbackend.entity.*;
import com.daelim.guildbackend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@jakarta.transaction.Transactional
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
        boardObj.remove("total");
        int partyId = partyRepository.save(party).getPartyId();

        PartyUser partyUser = new PartyUser();
        partyUser.setPartyId(partyId);
        partyUser.setUserId(boardObj.get("userId")+"");
        partyUserRepository.save(partyUser);

        Integer[] tagIds = g.fromJson(boardObj.get("tagId")+"", Integer[].class);
        String[] tagNames = g.fromJson(boardObj.get("tagName")+"", String[].class);
        boardObj.remove("tagId");
        boardObj.remove("tagName");

        // boardObj를 Board 객체로 변경 후 board 테이블에 저장
        Board board = objMpr.convertValue(boardObj, Board.class);
        board.setPartyId(partyId);
        Integer boardId = boardRepository.save(board).getBoardId();

        // 태그 존재 확인
        // 기존에 있던 태그면 tagBoard에 저장
        // 기존에 없던 태그면 생성 후 tagBoard 저장
        if (tagIds != null) {
            System.out.println(tagIds);
            for (Integer tagId :
                    tagIds) {
                TagBoard tb = new TagBoard();
                tb.setBoardId(boardId);
                tb.setTagId(tagId);
                tagBoardRepository.save(tb);
            }
        }
        if (tagNames != null) {
            System.out.println(tagNames);
            for (String tagName :
                    tagNames) {
                TagBoard tb = new TagBoard();
                tb.setBoardId(boardId);
                Optional<Tag> tagOp = tagRepository.findByTagName(tagName);
                Tag tag = null;
                if (tagOp.isPresent()){
                    tag = tagOp.get();
                } else {
                    tag = new Tag();
                    tag.setTagName(tagName);
                }
                Integer tagId = tagRepository.save(tag).getTagId();
                tb.setTagId(tagId);
                tagBoardRepository.save(tb);
            }
        }


        return boardId;
    }

    public List<Map<String, Object>> getAllBoards(Pageable pageable) {
        List<Map<String, Object>> results = new ArrayList<>();
        Page<Board> boards = boardRepository.findAll(pageable);
        boards.forEach(board -> {
            Map<String, Object> result = new HashMap<>();
            result.put("board", board);
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(board.getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.put("tags", tags);
            results.add(result);
        });

        return results;
    }

    public List<Map<String, Object>> getBoardsByUserId(Pageable pageable, String userId) {
        List<Map<String, Object>> results = new ArrayList<>();
        Page<Board> boards = boardRepository.findByUserId(pageable, userId);
        boards.forEach(board -> {
            Map<String, Object> result = new HashMap<>();
            result.put("board", board);
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(board.getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.put("tags", tags);
            results.add(result);
        });
        return results;
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
            tags.add(tagRepository.findById(tagBoard.getTagId()).get());
        });
        resultMap.put("Tags", tags);

        return resultMap;
    }

    public void updateBoard(Map<String, Object> boardObj, HttpSession session) {
        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(boardObj.get("userId"))) {
            Integer[] updateTagIds = g.fromJson(boardObj.get("tagId")+"", Integer[].class);
            boardObj.remove("tagId");
            String[] tagNames = g.fromJson(boardObj.get("tagName")+"", String[].class);
            Integer total = null;
            Integer boardId = (Integer) boardObj.get("boardId");
            TagBoard tb = new TagBoard();
            tb.setBoardId(boardId);

            if (updateTagIds != null) {
                tagBoardRepository.deleteAllByBoardId(boardId);
                for (Integer tagId:
                     updateTagIds) {
                    tb.setTagId(tagId);
                    tagBoardRepository.save(tb);
                    tb.setIdx(null);
                }
            }
            if (tagNames != null) {
                Tag tag = new Tag();
                Integer tagId = null;
                for (String tagName :
                        tagNames) {
                    tag.setTagName(tagName);
                    tagId = tagRepository.save(tag).getTagId();
                    tag.setTagId(null);
                    tb.setTagId(tagId);
                    tagBoardRepository.save(tb);
                    tb.setIdx(null);
                }
                boardObj.remove("tagName");
            }
            Party party = partyRepository.findById((Integer) boardObj.get("partyId")).get();
            if (boardObj.get("total") != null) {
                total = (Integer) boardObj.get("total");
                party.setTotal(total);
                partyRepository.save(party);
                boardObj.remove("total");
            }
            Board board = objMpr.convertValue(boardObj, Board.class);

            //board
            Board board1 = boardRepository.findById(board.getBoardId()).get();
            board.setWriteDate(board1.getWriteDate());
            board.setViews(board1.getViews());

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

    public List<Map<String, Object>> searchBoard(String text) {
        List<Map<String, Object>> results = new ArrayList<>();

        List<Board> boards = boardRepository.findByTitleLikeIgnoreCaseOrContentLikeIgnoreCase("%" + text + "%", "%" + text + "%");
        boards.forEach(board -> {
            Map<String, Object> result = new HashMap<>();
            result.put("board", board);
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(board.getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.put("tags", tags);

            results.add(result);
        });

        return results;
    }

    public List<Map<String, Object>> searchBoardByTagId(Integer tagId) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<TagBoard> tagBoards = tagBoardRepository.findByTagId(tagId);
        tagBoards.forEach(tagBoard -> {
            Map<String, Object> result = new HashMap<>();
            result.put("board", boardRepository.findById(tagBoard.getBoardId()).get());
            List<TagBoard> tagBoards1 = tagBoardRepository.findByBoardId(tagBoard.getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards1.forEach(tagBoard1 -> {
                tags.add(tagRepository.findById(tagBoard1.getTagId()).get());
            });
            result.put("tags", tags);

            results.add(result);
        });

        return results;
    }
}
