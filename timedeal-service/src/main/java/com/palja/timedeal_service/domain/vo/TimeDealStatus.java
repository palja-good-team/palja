package com.palja.timedeal_service.domain.vo;

import com.palja.timedeal_service.common.TimeDealEditableField;

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

        @Override
        public boolean canDelete() {
            return true;
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

        @Override
        public boolean canDelete() {
            return false;
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

        @Override
        public boolean canDelete() {
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

        @Override
        public boolean canDelete() {
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

    public abstract boolean canDelete();
}
