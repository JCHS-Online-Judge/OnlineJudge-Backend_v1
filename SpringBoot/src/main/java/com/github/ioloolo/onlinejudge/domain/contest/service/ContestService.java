package com.github.ioloolo.onlinejudge.domain.contest.service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.util.InviteCode;
import com.github.ioloolo.onlinejudge.domain.contest.data.Contest;
import com.github.ioloolo.onlinejudge.domain.contest.repository.ContestRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository repository;
    private final UserRepository userRepository;

    public Contest getContestInfo(String contestId, User user) throws Exception {

        Optional<Contest> contestOptional = repository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        if (!user.isAdmin() && !contest.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
        }

        if (!user.isAdmin()) {
            contest.setInviteCode(null);
        }

        return contest;
    }

    public void createContest(
            String title,
            String content,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) throws Exception {

        if (repository.existsByTitle(title)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_EXIST_CONTEST_TITLE);
        }

        if (startTime.isAfter(endTime)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
        }

        String inviteCode;
        do {
            inviteCode = InviteCode.generate();
        } while (repository.existsByInviteCode(inviteCode));

        Contest contest = Contest.builder()
                .title(title)
                .content(content)
                .inviteCode(inviteCode)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        repository.save(contest);
    }

    public String refreshInviteCode(String contestId) throws Exception {

        Optional<Contest> contestOptional = repository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        String inviteCode;
        do {
            inviteCode = InviteCode.generate();
        } while (repository.existsByInviteCode(inviteCode));

        contest.setInviteCode(inviteCode);

        repository.save(contest);

        return inviteCode;
    }

    public void updateContest(
            String contestId,
            String title,
            String content,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) throws Exception {

        Optional<Contest> contestOptional = repository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        contest.setTitle(title);
        contest.setContent(content);
        contest.setStartTime(startTime);
        contest.setEndTime(endTime);

        repository.save(contest);
    }

    public void deleteContest(String contestId) throws Exception {

        Optional<Contest> contestOptional = repository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        repository.delete(contest);
    }

    public List<User> getContestUsers(String contestId) throws Exception {

        Optional<Contest> contestOptional = repository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        return contest.getUsers();
    }

    public void joinContest(String inviteCode, User user) throws Exception {

        Optional<Contest> contestOptional = repository.findByInviteCode(inviteCode);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        if (contest.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_JOINED_CONTEST);
        }

        contest.getUsers().add(user);

        repository.save(contest);
    }

    public void leaveContest(String contestId, User user) throws Exception {

        Optional<Contest> contestOptional = repository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        if (!contest.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.NOT_JOINED_CONTEST);
        }

        contest.getUsers().remove(user);

        repository.save(contest);
    }

    public void forceJoinContest(String contestId, String userId) throws Exception {

        Optional<Contest> contestOptional = repository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.USER_NOT_FOUND);
        }
        User user = userOptional.get();

        if (contest.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_JOINED_CONTEST);
        }

        contest.getUsers().add(user);

        repository.save(contest);
    }

    public void forceLeaveContest(String contestId, String userId) throws Exception {

        Optional<Contest> contestOptional = repository.findById(contestId);
        if (contestOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
        }
        Contest contest = contestOptional.get();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw ExceptionFactory.of(ExceptionFactory.Type.USER_NOT_FOUND);
        }
        User user = userOptional.get();

        if (!contest.getUsers().contains(user)) {
            throw ExceptionFactory.of(ExceptionFactory.Type.NOT_JOINED_CONTEST);
        }

        contest.getUsers().remove(user);

        repository.save(contest);
    }
}
