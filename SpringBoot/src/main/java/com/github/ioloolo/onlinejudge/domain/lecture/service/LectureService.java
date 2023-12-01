package com.github.ioloolo.onlinejudge.domain.lecture.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.common.util.InviteCode;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import com.github.ioloolo.onlinejudge.domain.lecture.repository.LectureRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;
import com.github.ioloolo.onlinejudge.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureService {

	private final LectureRepository repository;
	private final UserRepository    userRepository;

	public Lecture getLectureInfo(String lectureId, User user) throws Exception {

		Optional<Lecture> lectureOptional = repository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

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

		Optional<Lecture> lectureOptional = repository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		String inviteCode;
		do {
			inviteCode = InviteCode.generate();
		} while (repository.existsByInviteCode(inviteCode));

		lecture.setInviteCode(inviteCode);

		repository.save(lecture);

		return inviteCode;
	}

	public void updateLecture(String lectureId, String title, String content) throws Exception {

		Optional<Lecture> lectureOptional = repository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		lecture.setTitle(title);
		lecture.setContent(content);

		repository.save(lecture);
	}

	public void deleteLecture(String lectureId) throws Exception {

		Optional<Lecture> lectureOptional = repository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		repository.delete(lecture);
	}

	public List<User> getLectureUsers(String lectureId) throws Exception {

		Optional<Lecture> lectureOptional = repository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		return lecture.getUsers();
	}

	public void joinLecture(String inviteCode, User user) throws Exception {

		Optional<Lecture> lectureOptional = repository.findByInviteCode(inviteCode);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		if (lecture.getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_JOINED_CONTEST);
		}

		lecture.getUsers().add(user);

		repository.save(lecture);
	}

	public void leaveLecture(String lectureId, User user) throws Exception {

		Optional<Lecture> lectureOptional = repository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		if (!lecture.getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.NOT_JOINED_CONTEST);
		}

		lecture.getUsers().remove(user);

		repository.save(lecture);
	}

	public void forceJoinLecture(String lectureId, String userId) throws Exception {

		Optional<Lecture> lectureOptional = repository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.USER_NOT_FOUND);
		}
		User user = userOptional.get();

		if (lecture.getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.ALREADY_JOINED_CONTEST);
		}

		lecture.getUsers().add(user);

		repository.save(lecture);
	}

	public void forceLeaveLecture(String lectureId, String userId) throws Exception {

		Optional<Lecture> lectureOptional = repository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.CONTEST_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.USER_NOT_FOUND);
		}
		User user = userOptional.get();

		if (!lecture.getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.NOT_JOINED_CONTEST);
		}

		lecture.getUsers().remove(user);

		repository.save(lecture);
	}
}
