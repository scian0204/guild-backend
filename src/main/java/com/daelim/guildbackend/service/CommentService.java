package com.daelim.guildbackend.service;

import com.daelim.guildbackend.dto.request.CommentDeleteRequest;
import com.daelim.guildbackend.entity.Comment;
import com.daelim.guildbackend.repository.CommentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    ObjectMapper objMpr = new ObjectMapper();
    @Autowired
    CommentRepository commentRepository;

    // 게시글별 댓글 목록
    public List<Comment> viewComment(Pageable pageable, Integer boardId) {
        return commentRepository.findAllByBoardId(pageable, boardId);
    }

    // 댓글 작성
    public Integer writeComment(Map<String, Object> commentObj) {
        Comment comment = objMpr.convertValue(commentObj, Comment.class);
        Integer commentId = commentRepository.save(comment).getCmtId();

        return commentId;
    }

    // 댓글 삭제
    public Boolean deleteComment(Map<String, Object> commentObj, HttpSession session) throws Exception {
        CommentDeleteRequest cdr = objMpr.convertValue(commentObj, CommentDeleteRequest.class);

        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(cdr.getUserId())) {
            commentRepository.deleteById(cdr.getCmtId());
            return true;
        } else {
            return false;
        }
    }

    // 댓글 수정
    public void updateComment(Map<String, Object> commentObj, HttpSession session) throws Exception {
        Comment comment = objMpr.convertValue(commentObj, Comment.class);

        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(comment.getUserId())) {
            Comment comment1 = commentRepository.findByCmtId(comment.getCmtId());
            comment.setWriteDate(comment1.getWriteDate());
            comment.setIsPublic(comment1.getIsPublic());
            commentRepository.save(comment);
        }
    }
}
