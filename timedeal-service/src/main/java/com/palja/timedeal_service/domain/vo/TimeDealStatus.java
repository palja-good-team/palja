package com.palja.timedeal_service.domain.vo;

import com.palja.common.exception.BusinessException;
import com.palja.timedeal_service.common.TimeDealEditableField;
import com.palja.timedeal_service.common.TimeDealErrorCode;

public enum TimeDealStatus {
    PENDING("대기중") {
        @Override
        public boolean canEditField(TimeDealEditableField field) {
            return true;
        }

        @Override
        public boolean canTransitTo(TimeDealStatus newStatus) {
            return newStatus == OPEN || newStatus == CLOSED;
        }
    },

    OPEN("진행중") {
        @Override
        public boolean canEditField(TimeDealEditableField field) {
            return field == TimeDealEditableField.END_AT;
        }

        @Override
        public boolean canTransitTo(TimeDealStatus newStatus) {
            return newStatus == CLOSED;
        }
    },

    SOLD_OUT("매진") {
        @Override
        public boolean canEditField(TimeDealEditableField field) {
            return false;
        }

        @Override
        public boolean canTransitTo(TimeDealStatus newStatus) {
            return false;
        }
    },

    CLOSED("종료") {
        @Override
        public boolean canEditField(TimeDealEditableField field) {
            return false;
        }

        @Override
        public boolean canTransitTo(TimeDealStatus newStatus) {
            return false;
        }
    };

    private final String description;

    TimeDealStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract boolean canEditField(TimeDealEditableField field);

    public abstract boolean canTransitTo(TimeDealStatus newStatus);

    // ========== 조건식 ==========
    public boolean isOpen() {
        return this == OPEN;
    }

    public boolean isClosed() {
        return this == CLOSED;
    }

    public boolean canRestoreStock() {
        return this == OPEN || this == SOLD_OUT;
    }

    public boolean canDelete() {
        return this == PENDING;
    }

    public static TimeDealStatus from(String status) {
        try {
            return TimeDealStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException(TimeDealErrorCode.INVALID_TIME_DEAL_STATUS);
        }
    }
}
