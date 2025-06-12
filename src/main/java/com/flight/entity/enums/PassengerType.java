package com.flight.entity.enums;

/**
 * 乘客类型枚举
 */
public enum PassengerType {
    PRIMARY("主要乘客"),
    ACCOMPANYING("随行乘客"),
    CHILD("儿童乘客"),
    INFANT("婴儿乘客");

    private final String description;

    PassengerType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
