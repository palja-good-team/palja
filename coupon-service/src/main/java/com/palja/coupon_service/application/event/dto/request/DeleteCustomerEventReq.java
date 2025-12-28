package com.palja.coupon_service.application.event.dto.request;

import com.palja.coupon_service.application.event.UserEvent;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteCustomerEventReq implements UserEvent {

	private Long userId;

	public static DeleteCustomerEventReq of(Long userId) {
		return DeleteCustomerEventReq.builder().userId(userId).build();
	}
}
