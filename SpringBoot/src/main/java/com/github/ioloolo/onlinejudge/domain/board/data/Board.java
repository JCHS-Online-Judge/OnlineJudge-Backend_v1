package com.github.ioloolo.onlinejudge.domain.board.data;

import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Document
public class Board {

    @Id
    private String id;

    private String title;
    private String content;

    private boolean isNotice;

    @DBRef
    private List<BoardFile> files;

    @DBRef
    private User author;

    private LocalDateTime createdTime;

    @DBRef
    private Lecture lecture;

    @DBRef
    private Contest contest;

    public boolean isCommon() {

        return lecture == null && contest == null;
    }

    public boolean isLecture() {

        return lecture != null && contest == null;
    }

    public boolean isContest() {

        return lecture == null && contest != null;
    }

    public Simple toSimple() {

        return Simple.builder().id(id).title(title).author(author).isNotice(isNotice).createdTime(createdTime).build();
    }

    @Data
    @EqualsAndHashCode(of = "id")
    @Builder
    public static class Simple {
        private String id;
        private String title;
        private User author;
        private boolean isNotice;
        private LocalDateTime createdTime;
    }
}
