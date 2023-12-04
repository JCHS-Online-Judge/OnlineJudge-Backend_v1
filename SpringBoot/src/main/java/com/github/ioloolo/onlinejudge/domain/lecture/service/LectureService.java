package com.github.ioloolo.onlinejudge.domain.lecture.service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.util.InviteCode;
import com.github.ioloolo.onlinejudge.common.util.OptionalParser;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import com.github.ioloolo.onlinejudge.domain.lecture.repository.LectureRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository repository;
    private final UserRepository userRepository;

    public Lecture getLectureInfo(String lectureId, User user) throws Exception {

        Lecture lecture = OptionalParser.parse(repository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);

        if (!user.isAdmin() && !lecture.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
        }

        if (!user.isAdmin()) {
            lecture.setInviteCode(null);
        }

        return lecture;
    }

    public void createLecture(String title, String content) throws Exception {

        if (repository.existsByTitle(title)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_EXIST_CONTEST_TITLE);
        }

        String inviteCode;
        do {
            inviteCode = InviteCode.generate();
        } while (repository.existsByInviteCode(inviteCode));

        Lecture lecture = Lecture.builder().title(title).content(content).inviteCode(inviteCode).build();

        repository.save(lecture);
    }

    public String refreshInviteCode(String lectureId) throws Exception {

        Lecture lecture = OptionalParser.parse(repository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);

        String inviteCode;
        do {
            inviteCode = InviteCode.generate();
        } while (repository.existsByInviteCode(inviteCode));

        lecture.setInviteCode(inviteCode);

        repository.save(lecture);

        return inviteCode;
    }

    public void updateLecture(String lectureId, String title, String content) throws Exception {

        Lecture lecture = OptionalParser.parse(repository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);

        lecture.setTitle(title);
        lecture.setContent(content);

        repository.save(lecture);
    }

    public void deleteLecture(String lectureId) throws Exception {

        Lecture lecture = OptionalParser.parse(repository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);

        repository.delete(lecture);
    }

    public List<User> getLectureUsers(String lectureId) throws Exception {

        Lecture lecture = OptionalParser.parse(repository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);

        return lecture.getUsers();
    }

    public void joinLecture(String inviteCode, User user) throws Exception {

        Lecture lecture = OptionalParser.parse(repository.findByInviteCode(inviteCode), ExceptionFactory.Type.LECTURE_NOT_FOUND);

        if (lecture.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_JOINED_CONTEST);
        }

        lecture.getUsers().add(user);

        repository.save(lecture);
    }

    public void leaveLecture(String lectureId, User user) throws Exception {

        Lecture lecture = OptionalParser.parse(repository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);

        if (!lecture.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.NOT_JOINED_CONTEST);
        }

        lecture.getUsers().remove(user);

        repository.save(lecture);
    }

    public void forceJoinLecture(String lectureId, String userId) throws Exception {

        Lecture lecture = OptionalParser.parse(repository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);
        User user = OptionalParser.parse(userRepository.findById(userId), ExceptionFactory.Type.USER_NOT_FOUND);

        if (lecture.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_JOINED_CONTEST);
        }

        lecture.getUsers().add(user);

        repository.save(lecture);
    }

    public void forceLeaveLecture(String lectureId, String userId) throws Exception {

        Lecture lecture = OptionalParser.parse(repository.findById(lectureId), ExceptionFactory.Type.LECTURE_NOT_FOUND);
        User user = OptionalParser.parse(userRepository.findById(userId), ExceptionFactory.Type.USER_NOT_FOUND);

        if (!lecture.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.NOT_JOINED_CONTEST);
        }

        lecture.getUsers().remove(user);

        repository.save(lecture);
    }
}
