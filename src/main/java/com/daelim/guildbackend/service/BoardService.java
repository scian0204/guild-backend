package com.daelim.guildbackend.service;

import com.daelim.guildbackend.controller.requestObject.BoardDeleteRequest;
import com.daelim.guildbackend.controller.responseObject.BoardListResponse;
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
                    boolean flag = false;
                    for (Integer tagId :
                            tagIds) {
                        if (tagOp.get().getTagId() == tagId) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        continue;
                    }
                    tag = tagOp.get();
                } else {
                    Integer tagId = null;
                    tag = new Tag();
                    tag.setTagName(tagName);
                    tagId = tagRepository.save(tag).getTagId();
                    tag.setTagId(tagId);
                }
                tb.setTagId(tag.getTagId());
                tagBoardRepository.save(tb);
            }
        }


        return boardId;
    }

    public List<BoardListResponse> getAllBoards(Pageable pageable) {
        List<BoardListResponse> results = new ArrayList<>();
        Page<Board> boards = boardRepository.findAll(pageable);
        boards.forEach(board -> {
            BoardListResponse result = new BoardListResponse();
            result.setBoard(board);
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(board.getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.setTags(tags);
            results.add(result);
        });

        return results;
    }

    public List<BoardListResponse> getBoardsByUserId(Pageable pageable, String userId) {
        List<BoardListResponse> results = new ArrayList<>();
        Page<Board> boards = boardRepository.findByUserId(pageable, userId);
        boards.forEach(board -> {
            BoardListResponse result = new BoardListResponse();
            result.setBoard(board);
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(board.getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.setTags(tags);
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
        resultMap.put("board", board);

        // Party
        Party party = partyRepository.findById(board.getPartyId()).get();
        resultMap.put("party", party);

        // List<Tag> - baordId로 tagBoard에서 목록 가져온 후 forEach를 이용하여 tagBoard의 tagId를 하나하나 검색하여 Tag 리스트에 삽입
        List<Tag> tags = new ArrayList<>();
        List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(boardId);
        tagBoards.forEach((tagBoard)->{
            tags.add(tagRepository.findById(tagBoard.getTagId()).get());
        });
        resultMap.put("tags", tags);

        return resultMap;
    }

    public void updateBoard(Map<String, Object> boardObj, HttpSession session) {
        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(boardObj.get("userId"))) {
            Integer[] updateTagIds = g.fromJson(boardObj.get("tagId")+"", Integer[].class);
            boardObj.remove("tagId");
            String[] tagNames = g.fromJson(boardObj.get("tagName")+"", String[].class);
            Integer total = null;
            Integer boardId = (Integer) boardObj.get("boardId");

            if (updateTagIds != null) {
                tagBoardRepository.deleteAllByBoardId(boardId);
                for (Integer tagId:
                     updateTagIds) {
                    TagBoard tb = new TagBoard();
                    tb.setBoardId(boardId);
                    tb.setTagId(tagId);
                    tagBoardRepository.save(tb);
                }
            }
            if (tagNames != null) {
                for (String tagName :
                        tagNames) {
                    TagBoard tb2 = new TagBoard();
                    tb2.setBoardId(boardId);
                    Optional<Tag> tagOp = tagRepository.findByTagName(tagName);
                    Tag tag = null;
                    if (tagOp.isPresent()){
                        boolean flag = false;
                        for (Integer tagId :
                                updateTagIds) {
                            if (tagOp.get().getTagId() == tagId) {
                                flag = true;
                            }
                        }
                        if (flag) {
                            continue;
                        }
                        tag = tagOp.get();
                    } else {
                        tag = new Tag();
                        tag.setTagName(tagName);
                        Integer tagId = tagRepository.save(tag).getTagId();
                        tag.setTagId(tagId);
                    }
                    tb2.setTagId(tag.getTagId());
                    tagBoardRepository.save(tb2);
                }
                boardObj.remove("tagName");
            }
            Board board = objMpr.convertValue(boardObj, Board.class);

            //board
            Board board1 = boardRepository.findById(board.getBoardId()).get();
            board.setWriteDate(board1.getWriteDate());
            board.setViews(board1.getViews());
            board.setPartyId(board1.getPartyId());

            Party party = partyRepository.findById(board.getPartyId()).get();
            if (boardObj.get("total") != null) {
                total = (Integer) boardObj.get("total");
                party.setTotal(total);
                partyRepository.save(party);
                boardObj.remove("total");
            }

            boardRepository.save(board);
        }
    }

    public String deleteBoardPost(Map<String, Object> boardObj, HttpSession session) { //게시물 삭제
        BoardDeleteRequest bdr = objMpr.convertValue(boardObj, BoardDeleteRequest.class);

        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(bdr.getUserId())) {
            tagBoardRepository.deleteAllByBoardId(bdr.getBoardId());
            boardRepository.deleteById(bdr.getBoardId());
            partyRepository.deleteById((Integer) boardObj.get("partyId"));
            return "0"; //userId 동일 = 삭제됨
        } else {
            return "1"; //
        }
    }

    public List<BoardListResponse> searchBoard(Pageable pageable, String text) {
        List<BoardListResponse> results = new ArrayList<>();

        List<Board> boards = boardRepository.findByTitleLikeIgnoreCaseOrContentLikeIgnoreCase(pageable, "%" + text + "%", "%" + text + "%");
        boards.forEach(board -> {
            BoardListResponse result = new BoardListResponse();
            result.setBoard(board);
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(board.getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.setTags(tags);

            results.add(result);
        });

        return results;
    }

    public List<BoardListResponse> searchBoardByTagId(Pageable pageable, Integer tagId) {
        List<BoardListResponse> results = new ArrayList<>();
        List<TagBoard> tagBoards = tagBoardRepository.findByTagId(pageable, tagId);
        tagBoards.forEach(tagBoard -> {
            BoardListResponse result = new BoardListResponse();
            result.setBoard(boardRepository.findById(tagBoard.getBoardId()).get());
            List<TagBoard> tagBoards1 = tagBoardRepository.findByBoardId(tagBoard.getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards1.forEach(tagBoard1 -> {
                tags.add(tagRepository.findById(tagBoard1.getTagId()).get());
            });
            result.setTags(tags);

            results.add(result);
        });

        return results;
    }
}
