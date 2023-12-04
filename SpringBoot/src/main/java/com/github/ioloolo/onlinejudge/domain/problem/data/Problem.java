package com.github.ioloolo.onlinejudge.domain.problem.data;

import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@Document
public class Problem {

    @Id
    private String id;

    private String problemNumber;
    private String title;

    private String description;
    private String inputDescription;
    private String outputDescription;

    private long timeLimit;
    private long memoryLimit;

    @DBRef
    private Lecture lecture;

    @DBRef
    private Contest contest;

    private List<TestCase> testCases;

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

        return Simple.builder().id(id).problemNumber(problemNumber).title(title).build();
    }

    @Data
    @EqualsAndHashCode(of = "id")
    @Builder
    public static class Simple {
        private String id;
        private String problemNumber;
        private String title;
    }

    @Data
    @Builder
    public static class TestCase {
        private String input;
        private String output;
        private boolean isSample;
    }
}
