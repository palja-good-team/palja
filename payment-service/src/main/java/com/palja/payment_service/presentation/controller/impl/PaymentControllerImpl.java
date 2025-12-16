package com.palja.payment_service.presentation.controller.impl;

import com.palja.common.annotation.RequiredInternal;
import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.response.CancelPaymentRes;
import com.palja.payment_service.application.dto.response.CreatePaymentRes;
import com.palja.payment_service.application.dto.response.ReadPaymentDetailRes;
import com.palja.payment_service.application.dto.response.ReadPaymentSummaryRes;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.presentation.controller.PaymentController;
import com.palja.payment_service.presentation.dto.request.CancelPaymentReq;
import com.palja.payment_service.presentation.dto.request.CompletePaymentReq;
import com.palja.payment_service.presentation.dto.request.CreatePaymentReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentControllerImpl implements PaymentController {

    private final PaymentService paymentService;

    @Override
    @PostMapping
    @RequiredRole({UserRole.MANAGER, UserRole.CUSTOMER})
    public ResponseEntity<ApiResponse<CreatePaymentRes>> createPayment(
            @Valid @RequestBody CreatePaymentReq req
    ) {
        CreatePaymentRes detail = paymentService.createPayment(req.toCommand(CurrentUser.getLoginId()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(detail, "결제가 생성되었습니다. (PENDING 상태)"));
    }

    @Override
    @PostMapping("/{paymentId}/complete")
    @RequiredRole({UserRole.MANAGER, UserRole.CUSTOMER})
    public ResponseEntity<ApiResponse<CreatePaymentRes>> completePayment(
            @PathVariable UUID paymentId,
            @Valid @RequestBody CompletePaymentReq req
    ) {
        CreatePaymentRes detail = paymentService.completePayment(req.toCommand(paymentId, CurrentUser.getLoginId()));

        return ResponseEntity
                .ok(ApiResponse.success(detail, "결제가 완료되었습니다."));
    }

    @Override
    @PostMapping("/{paymentId}/cancel")
    @RequiredRole({UserRole.MANAGER, UserRole.CUSTOMER})
    public ResponseEntity<ApiResponse<CancelPaymentRes>> cancelPayment(
            @PathVariable UUID paymentId,
            @RequestBody CancelPaymentReq req
    ){
        CancelPaymentRes detail = paymentService.cancelPayment(req.toCommand(paymentId, CurrentUser.getLoginId()));

        return ResponseEntity
                .ok(ApiResponse.success(detail,"결제가 취소되었습니다."));
    }

    @Override
    @GetMapping("/{paymentId}")
    @RequiredRole({UserRole.MANAGER, UserRole.CUSTOMER})
    public ResponseEntity<ApiResponse<ReadPaymentDetailRes>> getPayment(@PathVariable UUID paymentId) {
        ReadPaymentDetailRes detail = paymentService.getPayment(paymentId);

        return ResponseEntity
                .ok(ApiResponse.success(detail, "결제 단건 조회에 성공했습니다."));
    }

    @Override
    @GetMapping
    @RequiredRole({UserRole.MANAGER, UserRole.CUSTOMER})
    public ResponseEntity<ApiResponse<PageResponse<ReadPaymentSummaryRes>>> getPayments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        FindPaymentListByConditionCommand command = new FindPaymentListByConditionCommand(
                status,
                userId,
                orderId,
                startDate,
                endDate
        );

        PageRequest pageRequest = PageRequest.of(page, size);
        var pageResult = paymentService.searchPayments(command, pageRequest);

        PageResponse<ReadPaymentSummaryRes> detail = PageResponse.from(pageResult);

        return ResponseEntity
                .ok(ApiResponse.success(detail, "결제 목록 조회에 성공했습니다."));
    }

    @Override
    @GetMapping("/me")
    @RequiredRole({UserRole.CUSTOMER})
    public ResponseEntity<ApiResponse<PageResponse<ReadPaymentSummaryRes>>> getMyPayments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        FindPaymentListByConditionCommand command = new FindPaymentListByConditionCommand(
                status,
                null,
                null,
                startDate,
                endDate
        );

        PageRequest pageRequest = PageRequest.of(page, size);
        var pageResult = paymentService.searchPayments(command, pageRequest);

        PageResponse<ReadPaymentSummaryRes> detail = PageResponse.from(pageResult);

        return ResponseEntity
                .ok(ApiResponse.success(detail, "내 결제 목록 조회에 성공했습니다."));
    }

    @Override
    @DeleteMapping("/manager/{paymentId}")
    @RequiredRole({UserRole.MANAGER})
    public ResponseEntity<ApiResponse<String>> deletePayment(
            @PathVariable UUID paymentId
    ){
        paymentService.deletePayment(paymentId);
        return new ResponseEntity<>(ApiResponse.success("결제가 삭제되었습니다."),HttpStatus.OK);
    }
}
