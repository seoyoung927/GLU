package com.ssafy.glu.problem.domain.problem.domain;

import lombok.Getter;

@Getter
public enum QuestionTypeCode {
	QT01("객관식", String::equals),
	QT02("단답식", String::equalsIgnoreCase),
	QT03("서술형", String::contains);

	final String description;
	final GradingStrategy gradingStrategy;

	// 열거형 생성자에서 각기 다른 람다식을 받아 초기화
	QuestionTypeCode(String description, GradingStrategy gradingStrategy) {
		this.description = description;
		this.gradingStrategy = gradingStrategy;
	}

	// 채점 로직을 람다식으로 표현하기 위한 함수형 인터페이스
	@FunctionalInterface
	interface GradingStrategy {
		boolean isCorrect(String answer, String correctAnswer);
	}
}