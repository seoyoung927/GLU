package com.ssafy.glu.problem.domain.problem.controller;

import static com.ssafy.glu.problem.global.util.HeaderUtil.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.glu.problem.domain.problem.service.ProblemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems")
public class ProblemController {
	private final ProblemService problemService;

	@PostMapping("/{problemId}/favorite")
	public ResponseEntity<Void> createUserProblemFavorite(@RequestHeader(USER_ID) Long userId,
		@PathVariable("problemId") String problemId) {
		problemService.createUserProblemFavorite(userId, problemId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{problemId}/favorite")
	public ResponseEntity<Void> deleteUserProblemFavorite(@RequestHeader(USER_ID) Long userId,
		@PathVariable("problemId") String problemId) {
		problemService.deleteUserProblemFavorite(userId, problemId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}