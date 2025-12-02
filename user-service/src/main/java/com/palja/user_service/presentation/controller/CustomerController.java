package com.palja.user_service.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.palja.common.response.ApiResponse;
import com.palja.user_service.application.command.CreateCustomerCommand;
import com.palja.user_service.application.service.CustomerService;
import com.palja.user_service.presentation.dto.request.CreateCustomerReq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody CreateCustomerReq requestDto) {
		CreateCustomerCommand command = CreateCustomerReq.of(requestDto);
		customerService.createCustomer(command);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("일반 사용자가 생성되었습니다."));
	}

}
