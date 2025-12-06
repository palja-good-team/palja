package com.palja.timedeal_service.presentation.controller;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.ChangeTimeDealStatusCommand;
import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import com.palja.timedeal_service.application.command.UpdateTimeDealCommand;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.presentation.dto.request.ChangeTimeDealStatusReq;
import com.palja.timedeal_service.presentation.dto.request.CreateTimeDealReq;
import com.palja.timedeal_service.presentation.dto.request.UpdateTimeDealReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/time-deals")
@RequiredArgsConstructor
@Validated
public class TimeDealController {

    private final TimeDealService timeDealService;
    private final ResourceLoader resourceLoader;

    @PostMapping
    @RequiredRole({UserRole.MANAGER, UserRole.COMPANY_USER})
    public ResponseEntity<ApiResponse<TimeDealDetailRes>> createTimeDeal(
            @RequestBody @Valid CreateTimeDealReq req
    ) {
        log.info("POST api/v1/time-deals 타임딜 생성 요청");

        CreateTimeDealCommand command = req.toCommand(CurrentUser.getLoginId(), CurrentUser.getRole());

        TimeDealDetailRes res = timeDealService.createTimeDeal(command);

        log.info("타임딜 생성 성공: timeDealId = {}", res.getTimeDealId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(res, "타임딜이 생성되었습니다."));
    }

    @GetMapping("/{timeDealId}")
    public ResponseEntity<ApiResponse<TimeDealDetailRes>> getTimeDeal(@PathVariable UUID timeDealId) {
        log.info("GET /api/v1/time-deal/{} 타임딜 상세조회 요청", timeDealId);

        TimeDealDetailRes res = timeDealService.getTimeDeal(timeDealId);

        log.info("타임딜 상세 조회 성공 timeDealId = {}", res.getTimeDealId());
        return ResponseEntity.ok(ApiResponse.success(res, "타임딜 상세조회에 성공했습니다."));
    }

    @PutMapping("/{timeDealId}")
    @RequiredRole({UserRole.MANAGER, UserRole.COMPANY_USER})
    public ResponseEntity<ApiResponse<TimeDealDetailRes>> updateTimeDeal(
            @PathVariable UUID timeDealId,
            @RequestBody @Valid UpdateTimeDealReq req
    ) {
        log.info("PUT /api/v1/time-deals/{} 타임딜 수정 요청", timeDealId);

        UpdateTimeDealCommand command = req.toCommand(timeDealId, CurrentUser.getLoginId(), CurrentUser.getRole());

        TimeDealDetailRes res = timeDealService.updateTimeDeal(command);

        log.info("타임딜 수정 성공: timeDealId = {}", res.getTimeDealId());
        return ResponseEntity.ok(ApiResponse.success(res, "타임딜 수정에 성공했습니다."));
    }

    @PutMapping("/{timeDealId}/status")
    @RequiredRole({UserRole.MANAGER, UserRole.COMPANY_USER})
    public ResponseEntity<ApiResponse<TimeDealDetailRes>> changeTimeDealStatus(
            @PathVariable UUID timeDealId,
            @RequestBody @Valid ChangeTimeDealStatusReq req
    ) {
        log.info("PUT api/v1/time-deals/{}/status 타임딜 상태 변경 요청", timeDealId);

        ChangeTimeDealStatusCommand command = req.toCommand(timeDealId, CurrentUser.getLoginId(), CurrentUser.getRole());

        TimeDealDetailRes res = timeDealService.changeTimeDealStatus(command);

        log.info("타임딜 상태 변경 성공: timeDealId = {}", res.getTimeDealId());
        return ResponseEntity.ok(ApiResponse.success(res, "타임딜 상태 변경에 성공했습니다."));
    }
}
