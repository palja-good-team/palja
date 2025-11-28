package com.palja.user_service.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.palja.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manager extends BaseEntity {

	@Id
	@UuidGenerator
	@Column(name = "manager_id")
	private UUID id;

	@Column(name = "name", length = 10, nullable = false, unique = true)
	private String name;

	@Column(name = "email", nullable = false)
	private String email;

	@Builder
	private Manager(String name, String email) {
		this.name = name;
		this.email = email;
	}

}
