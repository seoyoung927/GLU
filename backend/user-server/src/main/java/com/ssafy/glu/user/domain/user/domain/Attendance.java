package com.ssafy.glu.user.domain.user.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "attendance_id")
	private Long id;

	private LocalDateTime attendanceDate;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;

	public Attendance(Users users) {
		this.attendanceDate = LocalDateTime.now();
		this.users = users;
	}
}
