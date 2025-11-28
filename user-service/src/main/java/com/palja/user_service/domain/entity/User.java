package com.palja.user_service.domain.entity;

import java.time.Instant;

import com.palja.user_service.domain.vo.UserRole;
import com.palja.user_service.domain.vo.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "login_id", length = 10, nullable = false, unique = true)
	private String loginId;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	// @JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private UserRole role;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	// @JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private UserStatus status;

	@Column(name = "deletedAt")
	private Instant deletedAt;

	@Column(name = "deletedBy")
	private Long deletedBy;

	@Builder
	private User(String loginId, String password, UserRole role) {
		this.loginId = loginId;
		this.password = password;
		this.role = role;
		this.status = this.role == UserRole.COMPANY_USER ? UserStatus.PENDING : UserStatus.ACTIVE;
	}

}
