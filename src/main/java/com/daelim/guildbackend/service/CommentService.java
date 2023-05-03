package com.daelim.guildbackend.service;

import com.daelim.guildbackend.entity.Comment;
import com.daelim.guildbackend.repository.CommentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentService {
    ObjectMapper objMpr = new ObjectMapper();
    @Autowired
    CommentRepository commentRepository;

    // 게시글별 댓글 목록
    public Comment viewComment(Integer boardId) {
        return commentRepository.findById(boardId).get();
    }

    // 댓글 작성
    public Integer writeComment(Map<String, Object> commentObj) {
        Comment comment = objMpr.convertValue(commentObj, Comment.class);
        Integer commentId = commentRepository.save(comment).getBoardId();

        return commentId;
    }

    // 댓글 삭제
    public String deleteComment(Map<String, Object> commentObj, HttpSession session) throws Exception {
        Integer cmtId = Integer.parseInt((String) commentObj.get("cmtId"));
        String userId = (String) commentObj.get("userId");

//        session.setAttribute("userId", "test");

        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(userId)) {
            commentRepository.deleteById(cmtId);
            return "0";
        } else {
            return "1";
        }
    }

    // 댓글 수정
    public void updateComment(Map<String, Object> commentObj, HttpSession session) throws Exception {
        Comment comment = objMpr.convertValue(commentObj, Comment.class);

//        session.setAttribute("userId", "test");

        if (session.getAttribute("userId") != null && session.getAttribute("userId").equals(comment.getUserId())) {
            Optional<Comment> optComment = commentRepository.findById(comment.getCmtId());
//            Comment comment1 = optComment.get();
            Comment comment1 = new Comment();
            comment.setComment(comment1.getComment());

            commentRepository.save(comment);
        }
    }
}
