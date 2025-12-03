package com.palja.user_service.presentation.controller;

import org.springframework.http.ResponseEntity;

import com.palja.common.response.ApiResponse;
import com.palja.user_service.presentation.dto.request.LoginUserReq;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthController {

	ResponseEntity<ApiResponse<Void>> login(LoginUserReq requestDto, HttpServletResponse response);

	ResponseEntity<ApiResponse<Void>> refresh(String accessToken, String refreshToken, HttpServletResponse response);

	ResponseEntity<ApiResponse<Void>> logout(String accessToken, HttpServletResponse response);

}
