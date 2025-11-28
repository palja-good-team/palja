package com.palja.user_service.presentation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.palja.user_service.application.service.ManagerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/managers")
@RequiredArgsConstructor
public class ManagerController {

	private final ManagerService managerService;

}
