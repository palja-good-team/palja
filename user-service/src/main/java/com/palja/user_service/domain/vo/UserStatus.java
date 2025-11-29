package com.palja.user_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

	PENDING("대기") {
		@Override
		public boolean canTransitionTo(UserStatus newStatus) {
			return newStatus == UserStatus.ACTIVE;
		}
	},

	ACTIVE("활성") {
		@Override
		public boolean canTransitionTo(UserStatus newStatus) {
			return false;
		}
	}
	;

	private final String description;

	public abstract boolean canTransitionTo(UserStatus newStatus);
}
