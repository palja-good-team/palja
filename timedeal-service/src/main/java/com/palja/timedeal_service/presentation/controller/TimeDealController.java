package com.palja.timedeal_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.timedeal_service.application.dto.*;
import com.palja.timedeal_service.presentation.dto.request.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "TimeDealController", description = "타임딜 API")
public interface TimeDealController {

    @Operation(summary = "타임딜 생성", description = "타임딜을 생성합니다.")
    ResponseEntity<ApiResponse<TimeDealCreateRes>> createTimeDeal(
            @RequestBody @Valid CreateTimeDealReq req
    );

    @Operation(summary = "타임딜 상세 조회", description = "타임딜 단건 상세 정보를 조회합니다.")
    ResponseEntity<ApiResponse<TimeDealDetailRes>> getTimeDeal(
            @Parameter(description = "타임딜 ID", required = true)
            @PathVariable UUID timeDealId
    );

    @Operation(summary = "타임딜 목록 조회", description = "타임딜 목록을 조회합니다.")
    ResponseEntity<ApiResponse<PageResponse<TimeDealDetailRes>>> getTimeDeals(
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "타임딜 수정", description = "타임딜 정보를 수정합니다.")
    ResponseEntity<ApiResponse<TimeDealUpdateRes>> updateTimeDeal(
            @Parameter(description = "타임딜 ID", required = true)
            @PathVariable UUID timeDealId,
            @RequestBody @Valid UpdateTimeDealReq req
    );

    @Operation(summary = "타임딜 상태 변경", description = "타임딜 상태를 변경합니다.)")
    ResponseEntity<ApiResponse<TimeDealStatusChangeRes>> changeTimeDealStatus(
            @Parameter(description = "타임딜 ID", required = true)
            @PathVariable UUID timeDealId,
            @RequestBody @Valid ChangeTimeDealStatusReq req
    );

    @Operation(summary = "타임딜 삭제", description = "타임딜을 삭제합니다. (PENDING 상태만 가능)")
    ResponseEntity<Void> deleteTimeDeal(
            @Parameter(description = "타임딜 ID", required = true)
            @PathVariable UUID timeDealId
    );

    @Hidden
    @Operation(summary = "타임딜 재고 차감 (내부)", description = "타임딜 남은 재고를 차감합니다. (내부 호출 전용)")
    ResponseEntity<Void> decreaseRemainingQuantity(
            @Parameter(description = "타임딜 ID", required = true)
            @PathVariable UUID timeDealId,
            @RequestBody @Valid DecreaseRemainingQuantityReq req
    );

    @Hidden
    @Operation(summary = "타임딜 재고 복구 (내부)", description = "타임딜 남은 재고를 복구합니다. (내부 호출 전용)")
    ResponseEntity<Void> restoreRemainingQuantity(
            @Parameter(description = "타임딜 ID", required = true)
            @PathVariable UUID timeDealId,
            @RequestBody @Valid RestoreRemainingQuantityReq req
    );

    @Hidden
    @Operation(summary = "업체 판매자 관련 타임딜 삭제 (내부)", description = "업체 판매자에 속한 모든 타임딜을 삭제합니다. (내부 호출 전용, PENDING 상태만 가능)")
    ResponseEntity<Void> deleteByCompanyUser(
            @Parameter(description = "업체 판매자 ID", required = true)
            @PathVariable UUID companyUserId
    );
}
