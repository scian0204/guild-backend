package com.daelim.guildbackend.service;

import com.daelim.guildbackend.dto.request.BoardDeleteRequest;
import com.daelim.guildbackend.dto.response.BoardListResponse;
import com.daelim.guildbackend.dto.response.BoardResponse;
import com.daelim.guildbackend.dto.response.Error;
import com.daelim.guildbackend.dto.response.Response;
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

    public Response<BoardResponse> write(Map<String, Object> boardObj) {
        Response<BoardResponse> res = new Response<>();
        BoardResponse result = new BoardResponse();

        // 파티 생성
        Party party = new Party();
        party.setTotal((Integer) boardObj.get("total"));
        boardObj.remove("total");
        result.setParty(partyRepository.save(party));
        int partyId = result.getParty().getPartyId();

        PartyUser partyUser = new PartyUser();
        partyUser.setPartyId(partyId);
        partyUser.setUserId(boardObj.get("userId")+"");
        partyUserRepository.save(partyUser);

        String[] tagNames = g.fromJson(boardObj.get("tagName")+"", String[].class);
        boardObj.remove("tagName");

        // boardObj를 Board 객체로 변경 후 board 테이블에 저장
        Board board = objMpr.convertValue(boardObj, Board.class);
        board.setPartyId(partyId);
        result.setBoard(boardRepository.save(board));
        Integer boardId = result.getBoard().getBoardId();

        // 태그 존재 확인
        // 기존에 있던 태그면 tagBoard에 저장
        // 기존에 없던 태그면 생성 후 tagBoard 저장
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
                    result.getTags().add(tagOp.get());
                } else {
                    Integer tagId = null;
                    tag = new Tag();
                    tag.setTagName(tagName);
                    Tag resultTag = tagRepository.save(tag);
                    result.getTags().add(resultTag);
                    tagId = resultTag.getTagId();
                    tag.setTagId(tagId);
                }
                tb.setTagId(tag.getTagId());
                tagBoardRepository.save(tb);
            }
        }

        res.setData(result);

        return res;
    }

    public Response<Page<BoardListResponse>> getAllBoards(Pageable pageable) {
        Response<Page<BoardListResponse>> res = new Response<>();
        Page<Board> boards = boardRepository.findAll(pageable);
        Page<BoardListResponse> results = new BoardListResponse().toDtoList(boards);
        results.forEach(result -> {
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(result.getBoard().getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.setTags(tags);
        });

        res.setData(results);

        return res;
    }

    public Response<Page<BoardListResponse>> getBoardsByUserId(Pageable pageable, String userId) {
        Response<Page<BoardListResponse>> res = new Response<>();
        Page<Board> boards = boardRepository.findByUserId(pageable, userId);
        Page<BoardListResponse> results = new BoardListResponse().toDtoList(boards);
        results.forEach(result -> {
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(result.getBoard().getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.setTags(tags);
        });

        res.setData(results);

        return res;
    }

    public Response<BoardResponse> viewBoard(Integer boardId) { //게시물 상세 페이지
        Response<BoardResponse> res = new Response<>();
        // Board - boardId로 검색 후 조회수 + 1
        Optional<Board> boardOps = boardRepository.findById(boardId);
        if (boardOps.isEmpty()) {
            Error error = new Error();
            error.setErrorId(0);
            error.setMessage("해당 게시글이 존재하지 않음");
            res.setError(error);
            return res;
        }
        Board board = boardOps.get();
        BoardResponse result = new BoardResponse();
        board.setViews(board.getViews() + 1);
        result.setBoard(boardRepository.save(board));

        // Party
        Party party = partyRepository.getReferenceById(board.getPartyId());
        result.setParty(party);

        // List<Tag> - baordId로 tagBoard에서 목록 가져온 후 forEach를 이용하여 tagBoard의 tagId를 하나하나 검색하여 Tag 리스트에 삽입
        List<Tag> tags = new ArrayList<>();
        List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(boardId);
        tagBoards.forEach((tagBoard)->{
            tags.add(tagRepository.getReferenceById(tagBoard.getTagId()));
        });
        result.setTags(tags);

        res.setData(result);

        return res;
    }

    public Response<BoardResponse> updateBoard(Map<String, Object> boardObj, HttpSession session) {
        Response<BoardResponse> res = new Response<>();
        if (session.getAttribute("userId") == null){
            Error error = new Error();
            error.setErrorId(0);
            error.setMessage("로그인상태가 아님");
            res.setError(error);
        } else if(!session.getAttribute("userId").equals(boardObj.get("userId"))) {
            Error error = new Error();
            error.setErrorId(1);
            error.setMessage("로그인된 아이디가 글 작성자의 아이디와 다름");
            res.setError(error);
        } else {
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

            res.setData(viewBoard(boardRepository.save(board).getBoardId()).getData());
        }

        return res;
    }

    public Response<Object> deleteBoardPost(Map<String, Object> boardObj, HttpSession session) { //게시물 삭제
        Response<Object> res = new Response<>();
        BoardDeleteRequest bdr = objMpr.convertValue(boardObj, BoardDeleteRequest.class);

        if (session.getAttribute("userId") == null) {
            Error error = new Error();
            error.setErrorId(0);
            error.setMessage("로그인상태가 아님");
            res.setError(error);
        } else if(!session.getAttribute("userId").equals(bdr.getUserId())) {
            Error error = new Error();
            error.setErrorId(1);
            error.setMessage("로그인상태가 아님");
            res.setError(error);
        } else {
            Board board = boardRepository.getReferenceById(bdr.getBoardId());
            tagBoardRepository.deleteAllByBoardId(bdr.getBoardId());
            boardRepository.deleteById(bdr.getBoardId());
            partyRepository.deleteById(board.getPartyId());
        }

        return res;
    }

    public Response<Page<BoardListResponse>> searchBoard(Pageable pageable, String text) {
        Response<Page<BoardListResponse>> res = new Response<>();
        Page<Board> boards = boardRepository.findByTitleLikeIgnoreCaseOrContentLikeIgnoreCase(pageable, "%" + text + "%", "%" + text + "%");
        Page<BoardListResponse> results = new BoardListResponse().toDtoList(boards);

        results.forEach(result -> {
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(result.getBoard().getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.setTags(tags);
        });

        res.setData(results);

        return res;
    }

    public Response<Page<BoardListResponse>> searchBoardByTagId(Pageable pageable, Integer tagId) {
        Response<Page<BoardListResponse>> res = new Response<>();
        Page<Board> boards = boardRepository.findByTagId(pageable, tagId);
        Page<BoardListResponse> results = new BoardListResponse().toDtoList(boards);

        results.forEach(result -> {
            List<TagBoard> tagBoards = tagBoardRepository.findByBoardId(result.getBoard().getBoardId());
            List<Tag> tags = new ArrayList<>();
            tagBoards.forEach(tagBoard -> {
                tags.add(tagRepository.findById(tagBoard.getTagId()).get());
            });
            result.setTags(tags);
        });

        res.setData(results);

        return res;
    }

}
