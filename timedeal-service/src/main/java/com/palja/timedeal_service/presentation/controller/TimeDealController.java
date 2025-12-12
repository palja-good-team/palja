package com.palja.timedeal_service.presentation.controller;

import brave.Response;
import com.palja.common.annotation.RequiredInternal;
import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.*;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.presentation.dto.request.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @PostMapping
    @RequiredRole({UserRole.MANAGER, UserRole.COMPANY_USER})
    public ResponseEntity<ApiResponse<TimeDealDetailRes>> createTimeDeal(
            @RequestBody @Valid CreateTimeDealReq req
    ) {
        log.info("POST api/v1/time-deals 타임딜 생성 요청");

        CreateTimeDealCommand command = req.toCommand(CurrentUser.getLoginId(), CurrentUser.getRole());

        TimeDealDetailRes res = timeDealService.createTimeDeal(command);

        log.info("타임딜 생성 완료: timeDealId = {}", res.getTimeDealId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(res, "타임딜이 생성되었습니다."));
    }

    @GetMapping("/{timeDealId}")
    public ResponseEntity<ApiResponse<TimeDealDetailRes>> getTimeDeal(@PathVariable UUID timeDealId) {
        log.info("GET /api/v1/time-deal/{} 타임딜 상세조회 요청", timeDealId);

        TimeDealDetailRes res = timeDealService.getTimeDeal(timeDealId);

        log.info("타임딜 상세 조회 완료 timeDealId = {}", res.getTimeDealId());
        return ResponseEntity.ok(ApiResponse.success(res, "타임딜 상세 조회에 성공했습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TimeDealDetailRes>>> getTimeDeals(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("GET api/v1/time-deals 타임딜 목록 조회 요청");

        PageResponse<TimeDealDetailRes> res = timeDealService.getTimeDeals(pageable);

        log.info("타임딜 목록 조회 완료");
        return ResponseEntity.ok(ApiResponse.success(res, "타임딜 목록 조회에 성공했습니다."));
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

        log.info("타임딜 수정 완료: timeDealId = {}", res.getTimeDealId());
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

        log.info("타임딜 상태 변경 완료: timeDealId = {}", res.getTimeDealId());
        return ResponseEntity.ok(ApiResponse.success(res, "타임딜 상태 변경에 성공했습니다."));
    }

    @DeleteMapping("/{timeDealId}")
    public ResponseEntity<Void> deleteTimeDeal(@PathVariable UUID timeDealId) {
        log.info("DELETE api/v1/time-deals/{} 타임딜 삭제 요청", timeDealId);

        DeleteTimeDealCommand command = DeleteTimeDealCommand.of(timeDealId, CurrentUser.getLoginId(), CurrentUser.getRole());

        timeDealService.deleteTimeDeal(command);

        log.info("타임딜 삭제 완료");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{timeDealId}/stock/decrease")
    @RequiredInternal
    public ResponseEntity<Void> decreaseRemainingQuantity(
            @PathVariable UUID timeDealId,
            @RequestBody @Valid DecreaseRemainingQuantityReq req
    ) {
        log.info("PUT api/v1/time-deals/{}/stock/decrease 타임딜 남은 재고 차감 요청", timeDealId);

        DecreaseRemainingQuantityCommand command = req.toCommand(timeDealId);

        timeDealService.decreaseRemainingQuantity(command);

        log.info("타임딜 남은 재고 차감 완료");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{timeDealId}/stock/restore")
    @RequiredInternal
    public ResponseEntity<Void> restoreRemainingQuantity(
            @PathVariable UUID timeDealId,
            @RequestBody @Valid RestoreRemainingQuantityReq req
    ) {
        log.info("PUT api/v1/time-deals/{}/stock/restore 타임딜 남은 재고 복구 요청", timeDealId);

        RestoreRemainingQuantityCommand command = req.toCommand(timeDealId);

        timeDealService.restoreRemainingQuantity(command);

        log.info("타임딜 남은 재고 복구 완료");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/company-users/{companyUserId}")
    @RequiredInternal
    public ResponseEntity<Void> deleteByCompanyUser(@PathVariable UUID companyUserId) {
        log.info("DELETE api/v1/time-deals/{} 업체 판매자 관련 타임딜 삭제 요청", companyUserId);

        timeDealService.deleteByCompanyUser(companyUserId);

        log.info("업체 판매자 관련 타임딜 삭제 완료");
        return ResponseEntity.noContent().build();
    }
}
