package com.palja.user_service.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.palja.common.entity.BaseEntity;

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
@Table(name = "p_customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity {

	@Id
	@UuidGenerator
	@Column(name = "customer_id")
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "name", length = 10, nullable = false, unique = true)
	private String name;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "address", nullable = false)
	private String address;

	@Builder
	private Customer(String name, String email, String address) {
		this.name = name;
		this.email = email;
		this.address = address;
	}

}
