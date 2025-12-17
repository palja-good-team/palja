package com.palja.user_service.application.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteCustomerEvent implements UserEvent {

	private Long userId;

	@Override
	public String topic() {
		return "customer.delete.request";
	}

	public static DeleteCustomerEvent from(Long userId) {
		return DeleteCustomerEvent.builder().userId(userId).build();
	}

}
