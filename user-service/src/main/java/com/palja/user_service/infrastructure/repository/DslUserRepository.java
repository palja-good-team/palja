package com.palja.user_service.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.palja.common.vo.UserRole;
import com.palja.user_service.domain.entity.QUser;
import com.palja.user_service.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DslUserRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Page<User> searchAllManagers(String loginId, String email, String name, Pageable pageable) {
		QUser qUser = QUser.user;

		List<User> managers = jpaQueryFactory
			.selectFrom(qUser)
			.where(
				loginId != null ? qUser.loginId.contains(loginId) : null,
				email != null ? qUser.email.contains(email) : null,
				name != null ? qUser.name.contains(name) : null,
				qUser.role.eq(UserRole.MANAGER),
				qUser.deletedAt.isNull()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = getTotal(qUser, UserRole.MANAGER, loginId, email, name);

		return new PageImpl<>(managers, pageable, total != null ? total : 0);
	}

	public Page<User> searchAllCustomers(String loginId, String email, String name, Pageable pageable) {
		QUser qUser = QUser.user;

		List<User> customers = jpaQueryFactory
			.selectFrom(qUser)
			.where(
				loginId != null ? qUser.loginId.contains(loginId) : null,
				email != null ? qUser.email.contains(email) : null,
				name != null ? qUser.name.contains(name) : null,
				qUser.role.eq(UserRole.CUSTOMER),
				qUser.deletedAt.isNull()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = getTotal(qUser, UserRole.CUSTOMER, loginId, email, name);

		return new PageImpl<>(customers, pageable, total != null ? total : 0);
	}

	private Long getTotal(QUser qUser, UserRole role, String loginId, String email, String name) {
		return jpaQueryFactory
			.select(qUser.count())
			.from(qUser)
			.where(
				loginId != null ? qUser.loginId.contains(loginId) : null,
				email != null ? qUser.email.contains(email) : null,
				name != null ? qUser.name.contains(name) : null,
				qUser.role.eq(role),
				qUser.deletedAt.isNull()
			)
			.fetchOne();
	}

}
