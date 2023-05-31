package com.daelim.guildbackend.dto.response;

import com.daelim.guildbackend.entity.Board;
import com.daelim.guildbackend.entity.Tag;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class BoardListResponse {
    Board board;
    List <com.daelim.guildbackend.entity.Tag> tags;

    BoardListResponse(Board board, List<Tag> tags) {
        this.board = board;
        this.tags = tags;
    }

    public BoardListResponse() {

    }

    public static BoardListResponseBuilder builder() {
        return new BoardListResponseBuilder();
    }

    public Page<BoardListResponse> toDtoList(Page<Board> boardList){
        Page<BoardListResponse> boardDtoList = boardList.map(m -> BoardListResponse.builder()
                .board(m)
                .build());
        return boardDtoList;
    }

    public static class BoardListResponseBuilder {
        private Board board;
        private List<Tag> tags;

        BoardListResponseBuilder() {
        }

        public BoardListResponseBuilder board(Board board) {
            this.board = board;
            return this;
        }

        public BoardListResponseBuilder tags(List<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public BoardListResponse build() {
            return new BoardListResponse(board, tags);
        }

        public String toString() {
            return "BoardListResponse.BoardListResponseBuilder(board=" + this.board + ", tags=" + this.tags + ")";
        }
    }
}
