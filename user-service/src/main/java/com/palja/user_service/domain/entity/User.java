package com.palja.user_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.common.vo.UserRole;
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
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "login_id", length = 10, nullable = false, unique = true)
	private String loginId;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "name", length = 10, nullable = false)
	private String name;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	// @JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private UserRole role;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	// @JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private UserStatus status;

	@Builder
	private User(String loginId, String password, String name, String email, String address, UserRole role) {
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.email = email;
		this.address = address;
		this.role = role;
		this.status = this.role == UserRole.COMPANY_USER ? UserStatus.PENDING : UserStatus.ACTIVE;
	}

	public void updateStatus(String status) {
		UserStatus newStatus = validateAndGetStatus(status);
		if (!this.status.canTransitionTo(newStatus)) {
			throw new IllegalArgumentException(("%s 상태에서 %s 상태로 변경할 수 없습니다.")
				.formatted(this.status.getDescription(), newStatus.getDescription()));
		}

		this.status = newStatus;
	}

	private UserStatus validateAndGetStatus(String status) {
		try {
			return UserStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("유효하지 않은 상태 값 입니다.");
		}
	}

}
