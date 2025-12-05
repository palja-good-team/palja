package com.palja.user_service.domain.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.palja.common.auditor.AuditorContext;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_company_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyUser {

	@Id
	@UuidGenerator
	@Column(name = "company_user_id")
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "company_name", length = 50, nullable = false)
	private String companyName;

	@Column(name = "company_number", length = 12, nullable = false)
	private String companyNumber;

	@Column(name = "deletedAt")
	private Instant deletedAt;

	@Column(name = "deletedBy", length = 10)
	private String deletedBy;

	public void softDelete() {
		this.deletedAt = Instant.now();
		this.deletedBy = AuditorContext.get().getLoginId();
		user.softDelete();
	}

	@Builder
	private CompanyUser(User user, String companyName, String companyNumber) {
		this.user = user;
		this.companyName = companyName;
		this.companyNumber = companyNumber;
	}

	public void update(String companyName, String address) {
		if (companyName != null && !companyName.isBlank()) {
			this.companyName = companyName;
		}
		this.user.update(address);
	}

	public void updateStatus(String status) {
		this.user.updateStatus(status);
	}

}
