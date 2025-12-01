package com.palja.timedeal_service.domain.vo;

public enum TimeDealStatus {
    PENDING("대기중"),
    OPEN("진행중"),
    SOLD_OUT("매진"),
    CLOSED("종료");

    private final String description;

    TimeDealStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
