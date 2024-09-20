package com.ssafy.glu.user.domain.user.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record UserResponse (
	Long id,
	String nickname,
	Integer stage,
	Integer exp,
	String imageUrl,
	Integer dayCount,
	List<UserProblemTypeResponse> problemTypeList
){}
