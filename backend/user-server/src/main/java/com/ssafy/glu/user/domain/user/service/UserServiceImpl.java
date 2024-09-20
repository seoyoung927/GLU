package com.ssafy.glu.user.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ssafy.glu.user.domain.user.config.LevelConfig;
import com.ssafy.glu.user.domain.user.domain.Attendance;
import com.ssafy.glu.user.domain.user.domain.ProblemType;
import com.ssafy.glu.user.domain.user.domain.UserProblemType;
import com.ssafy.glu.user.domain.user.domain.Users;
import com.ssafy.glu.user.domain.user.dto.request.AttendanceRequest;
import com.ssafy.glu.user.domain.user.dto.request.ExpUpdateRequest;
import com.ssafy.glu.user.domain.user.dto.request.UserRegisterRequest;
import com.ssafy.glu.user.domain.user.dto.request.UserUpdateRequest;
import com.ssafy.glu.user.domain.user.dto.response.AttendanceResponse;
import com.ssafy.glu.user.domain.user.dto.response.ExpUpdateResponse;
import com.ssafy.glu.user.domain.user.dto.response.UserProblemTypeResponse;
import com.ssafy.glu.user.domain.user.dto.response.UserResponse;
import com.ssafy.glu.user.domain.user.exception.DateInValidException;
import com.ssafy.glu.user.domain.user.exception.UserNotFoundException;
import com.ssafy.glu.user.domain.user.repository.AttendanceRepository;
import com.ssafy.glu.user.domain.user.repository.UserProblemTypeRepository;
import com.ssafy.glu.user.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserProblemTypeRepository userProblemTypeRepository;
	private final AttendanceRepository attendanceRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	private final LevelConfig levelConfig;

	/**
	 * 유저 저장
	 * 저장할때 문제타입도 만들어서 같이 저장시키기
	 * PT01	어휘 및 문법
	 * PT02	독해 
	 * PT03	추론
	 */
	@Transactional
	@Override
	public Long register(UserRegisterRequest userRegisterRequest) {

		String encodedPassword = passwordEncoder.encode(userRegisterRequest.password());

		Users user = Users.builder()
			.loginId(userRegisterRequest.id())
			.nickname(userRegisterRequest.nickname())
			.password(encodedPassword)
			.birth(userRegisterRequest.birth())
			.build();

		//유저 저장
		Users saveUser = userRepository.save(user);

		// 유저 문제타입 저장
		for (ProblemType problemType : ProblemType.values()) {

			UserProblemType userProblemType = UserProblemType.builder()
				.problemTypeCode(problemType)
				.user(saveUser)
				.build();

			userProblemTypeRepository.save(userProblemType);
		}

		return saveUser.getId();
	}

	/**
	 * 유저 정보 가져오기
	 */
	@Override
	public UserResponse getUser(Long userId) {

		Users findUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		List<UserProblemType> userProblemTypes = userProblemTypeRepository.findAllByUserId(userId);

		List<String> levelImages = levelConfig.getImages();
		String userImage = levelImages.get(findUser.getStage());

		return UserResponse.builder()
			.id(userId)
			.dayCount(findUser.getDayCount())
			.stage(findUser.getStage())
			.exp(findUser.getExp())
			.imageUrl(userImage)
			.nickname(findUser.getNickname())
			.problemTypeList(getProblemTypeLists(userProblemTypes))
			.build();
	}

	/**
	 * 변환 시키기 userProblemlist => ProblemTypeList
	 */
	private static List<UserProblemTypeResponse> getProblemTypeLists(List<UserProblemType> userProblemTypes) {
		return userProblemTypes.stream().map(UserProblemTypeResponse::of).toList();
	}

	/**
	 * 유저 업데이트
	 * 비밀번호 일치 체크후 업데이트
	 */
	@Transactional
	@Override
	public void updateUser(Long userId, UserUpdateRequest request) {

		Users findUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		//비밀번호 일치할때
		if (passwordEncoder.matches(request.password(), findUser.getPassword())) {
			String encodedPassword =
				StringUtils.hasText(request.newPassword()) ? passwordEncoder.encode(request.newPassword()) : null;

			//유저 정보 업데이트
			findUser.updateUser(encodedPassword, request.nickname());
		}
	}

	/**
	 * 유저 삭제
	 * softdelete를 위해 user의 isdelete만 변경
	 */
	@Transactional
	@Override
	public void deleteUser(Long userId) {
		Users findUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		//유저 삭제
		findUser.deleteUser();
	}

	@Override
	public Boolean checkUser(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}

	/**
	 * 출석정보 가져오기
	 */
	@Override
	public List<AttendanceResponse> getAttendance(Long userId, AttendanceRequest request) {
		//날짜 체크
		if (request.year() < 1900 || request.year() > 2100 || request.month() < 1 || request.month() > 12) {
			throw new DateInValidException();
		}
		return attendanceRepository.countAttendanceByYearAndMonth(userId, request);
	}

	/**
	 * 문제 풀면 출석하기
	 */
	public int attend(Long userId, Integer solveNum) {

		Users findUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		// 가장 최근 출석 기록을 가져옴
		Optional<Attendance> lastAttendOpt = attendanceRepository.findFirstByUsersIdOrderByAttendanceDateDesc(userId);

		// 오늘 날짜
		LocalDateTime today = LocalDateTime.now();

		findUser.updateDayCount();

		// 출석이 없거나, 출석 기록이 오늘 날짜와 다르면 새로운 출석 기록을 생성
		if (lastAttendOpt.isEmpty() || !isSameDay(lastAttendOpt.get().getAttendanceDate(), today)) {
			Attendance newAttendance = Attendance.builder()
				.users(findUser)
				.todaySolve(solveNum)
				.attendanceDate(today)
				.build();
			attendanceRepository.save(newAttendance);
		} else {
			// 출석 기록이 오늘 날짜와 같으면 오늘 문제 푼 수를 업데이트
			Attendance lastAttend = lastAttendOpt.get();
			lastAttend.updateTodaySolve(solveNum);
			return 0;
		}

		return Math.min(10, 2 * findUser.getDayCount());
	}

	private boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
		return date1.toLocalDate().isEqual(date2.toLocalDate());
	}

	@Override
	public ExpUpdateResponse updateExp(Long userId, ExpUpdateRequest expUpdateRequest) {

		List<String> images = levelConfig.getImages();

		Users findUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		Integer before = findUser.getStage();

		// 점수 계산 로직
		int upScore = expUpdateRequest.problemInfoList().stream()
			.mapToInt(problemInfo -> calculateScore(expUpdateRequest.userProblemTypeLevels(), problemInfo))
			.sum();

		//출석 점수
		int attendScore = attend(userId, expUpdateRequest.problemInfoList().size());

		Integer after = findUser.updateScore(upScore + attendScore);

		return new ExpUpdateResponse(before != after, before != after ? images.get(before) : images.get(after));
	}

	private int calculateScore(Map<String, Integer> userLevels, ExpUpdateRequest.ProblemInfo problemInfo) {
		int userLevel = userLevels.getOrDefault(problemInfo.code(), 0);
		if (userLevel < problemInfo.level()) return 3;
		else if (userLevel == problemInfo.level()) return 2;
		else return 1;
	}

}
