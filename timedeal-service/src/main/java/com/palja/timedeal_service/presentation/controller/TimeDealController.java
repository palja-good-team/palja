package com.palja.timedeal_service.presentation.controller;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.presentation.dto.request.CreateTimeDealReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/time-deals")
@RequiredArgsConstructor
@Validated
public class TimeDealController {

    private final TimeDealService timeDealService;

    @PostMapping
    public ResponseEntity<ApiResponse<TimeDealDetailRes>> createTimeDeal(
            @RequestBody @Valid CreateTimeDealReq req
    ) {
        log.info("POST api/v1/time-deals 타임딜 생성 요청");

        String loginId = CurrentUser.getLoginId();
        UserRole role = CurrentUser.getRole();

        CreateTimeDealCommand command = req.toCommand(loginId, role);

        TimeDealDetailRes res = timeDealService.createTimeDeal(command);

        log.info("타임딜 생성 성공: timeDealId = {}", res.getTimeDealId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(res, "타임딜이 생성되었습니다."));
    }
}
