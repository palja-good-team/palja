package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.vo.Recipient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 수령인 정보 응답
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "수령인 정보")
public class RecipientRes {

    @Schema(description = "수령인 이름", example = "김차돌")
    private String name;

    @Schema(description = "수령인 이메일", example = "kim@example.com")
    private String email;

    @Schema(description = "수령인 주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "배송 요청사항", example = "부재 시 문 앞에 놓아주세요")
    private String deliveryMessage;

    public static RecipientRes from(Recipient recipient) {
        return RecipientRes.builder()
                .name(recipient.getName())
                .email(recipient.getEmail())
                .address(recipient.getAddress())
                .deliveryMessage(recipient.getDeliveryMessage())
                .build();
    }
}