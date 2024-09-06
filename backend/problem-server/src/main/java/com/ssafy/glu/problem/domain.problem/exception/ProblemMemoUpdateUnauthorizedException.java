package com.ssafy.glu.problem.domain.problem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ssafy.glu.problem.global.error.ErrorCode;
import com.ssafy.glu.problem.global.error.ServiceException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ProblemMemoUpdateUnauthorizedException extends ServiceException {
	public ProblemMemoUpdateUnauthorizedException() {
		super(ErrorCode.PROBLEM_MEMO_UPDATE_UNAUTHORIZED);
	}

	public ProblemMemoUpdateUnauthorizedException(Exception exception) {
		super(ErrorCode.PROBLEM_MEMO_UPDATE_UNAUTHORIZED, exception);
	}
}
