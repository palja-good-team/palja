package com.palja.order_service.application.dto;

import com.palja.order_service.infrastructure.external.dto.response.PaymentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentRes {

    private final UUID paymentId;

    public static PaymentRes from(PaymentDTO dto) {
        return new PaymentRes(dto.getPaymentId());
    }
}