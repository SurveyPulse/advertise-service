package com.example.advertise_service.exception;

import com.example.global.exception.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdvertisementExceptionType implements ExceptionType {
    ADVERTISEMENT_NOT_FOUND(5201, "해당 광고를 찾을 수 없습니다."),
    INVALID_ADVERTISEMENT_DATA(5202, "광고 데이터가 올바르지 않습니다."),
    DUPLICATE_ADVERTISEMENT_ENTRY(5203, "이미 등록된 광고입니다."),
    ADVERTISEMENT_CREATION_FAILED(5204, "광고 등록에 실패하였습니다.");

    private final int statusCode;
    private final String message;
}
