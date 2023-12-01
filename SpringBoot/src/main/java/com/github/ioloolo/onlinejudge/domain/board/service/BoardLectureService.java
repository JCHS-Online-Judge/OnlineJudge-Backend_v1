package com.github.ioloolo.onlinejudge.domain.board.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.ioloolo.onlinejudge.common.exception.ExceptionFactory;
import com.github.ioloolo.onlinejudge.domain.board.data.Board;
import com.github.ioloolo.onlinejudge.domain.board.data.BoardFile;
import com.github.ioloolo.onlinejudge.domain.board.repository.BoardFileRepository;
import com.github.ioloolo.onlinejudge.domain.board.repository.BoardRepository;
import com.github.ioloolo.onlinejudge.domain.lecture.data.Lecture;
import com.github.ioloolo.onlinejudge.domain.lecture.repository.LectureRepository;
import com.github.ioloolo.onlinejudge.domain.user.data.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardLectureService {

	private final BoardRepository     repository;
	private final BoardFileRepository fileRepository;
	private final LectureRepository   lectureRepository;

	public List<Board.Simple> getBoards(String lectureId, User user) throws Exception {

		Optional<Lecture> lectureOptional = lectureRepository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.LECTURE_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		if (!user.isAdmin() && !lecture.getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}

		return repository.findAll()
				.stream()
				.filter(Board::isLecture)
				.filter(board -> board.getLecture().equals(lecture))
				.map(Board::toSimple)
				.toList();
	}

	public Board getBoardInfo(String boardId, User user) throws Exception {

		Optional<Board> boardOptional = repository.findById(boardId);
		if (boardOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.BOARD_NOT_FOUND);
		}
		Board board = boardOptional.get();

		if (!board.isLecture()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.BAD_REQUEST);
		}
		if (!user.isAdmin() && !board.getLecture().getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}

		return board;
	}

	public void createBoard(
			String lectureId, String title, String content, boolean isNotice, List<String> files, User author
	) throws Exception {

		Optional<Lecture> lectureOptional = lectureRepository.findById(lectureId);
		if (lectureOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.LECTURE_NOT_FOUND);
		}
		Lecture lecture = lectureOptional.get();

		if (!lecture.getUsers().contains(author)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}

		files = files == null ? List.of() : files;
		List<BoardFile> files1 = files.stream()
				.map(fileRepository::findById)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();

		Board board = Board.builder()
				.title(title)
				.content(content)
				.isNotice(isNotice)
				.files(files1)
				.author(author)
				.createdTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
				.lecture(lecture)
				.build();

		repository.save(board);
	}

	public void updateBoard(
			String boardId, String title, String content, boolean isNotice, List<String> files, User author
	) throws Exception {

		Optional<Board> boardOptional = repository.findById(boardId);
		if (boardOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.BOARD_NOT_FOUND);
		}
		Board board = boardOptional.get();

		if (!author.isAdmin() && !board.getLecture().getUsers().contains(author)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}
		if (!board.getAuthor().equals(author)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}

		if (isNotice && !author.isAdmin()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}

		files = files == null ? List.of() : files;
		List<BoardFile> files1 = files.stream()
				.map(fileRepository::findById)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();

		board.setTitle(title);
		board.setContent(content);
		board.setNotice(isNotice);
		board.setFiles(files1);

		repository.save(board);
	}

	public void deleteBoard(String id, User user) throws Exception {

		Optional<Board> boardOptional = repository.findById(id);
		if (boardOptional.isEmpty()) {
			throw ExceptionFactory.of(ExceptionFactory.Type.BOARD_NOT_FOUND);
		}
		Board board = boardOptional.get();

		if (!user.isAdmin() && !board.getLecture().getUsers().contains(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}
		if (!user.isAdmin() && !board.getAuthor().equals(user)) {
			throw ExceptionFactory.of(ExceptionFactory.Type.UNAUTHORIZED);
		}

		repository.delete(board);
	}
}
