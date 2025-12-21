package com.palja.user_service.application.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ReadQueueRankRes {

	private String loginId;
	private Long rank;

	public static ReadQueueRankRes from(String loginId, Long rank) {
		return ReadQueueRankRes.builder()
			.loginId(loginId)
			.rank(rank)
			.build();
	}

}
