package com.palja.user_service.application.event.dto.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteCustomerEventReq {

	private Long userId;

	public static DeleteCustomerEventReq from(Long userId) {
		return DeleteCustomerEventReq.builder().userId(userId).build();
	}

}
