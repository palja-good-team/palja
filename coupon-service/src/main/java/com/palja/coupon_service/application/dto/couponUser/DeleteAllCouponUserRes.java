package com.palja.coupon_service.application.dto.couponUser;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class DeleteAllCouponUserRes {
    List<UUID> deletedCouponUserIds;
    int deletedCount;

    public static DeleteAllCouponUserRes from(List<UUID> deletedCouponUserIds, int deleteCount) {
        return DeleteAllCouponUserRes.builder()
                .deletedCouponUserIds(deletedCouponUserIds)
                .deletedCount(deleteCount)
                .build();
    }
}
