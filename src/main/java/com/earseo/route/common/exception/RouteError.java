package com.earseo.route.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RouteError implements ErrorCodeInterface {
    INVALID_PLACE_IDS("RUT001", "최소 1개 이상의 관광지 ID가 필요합니다.", HttpStatus.BAD_REQUEST),
    ROUTE_NOT_FOUND("RUT002", "존재하지 않는 경로입니다.", HttpStatus.NOT_FOUND),
    ROUTE_NOT_OWNER("RUT003", "해당 사용자의 경로가 아닙니다.", HttpStatus.FORBIDDEN),
    ROUTE_NOT_IN_PROGRESS("RUT004", "진행 중이 아닌 경로는 완료할 수 없습니다.", HttpStatus.CONFLICT),

    ROUTE_ITEM_NOT_IN_PROGRESS("RUT005", "진행 중인 경로의 아이템만 방문 처리할 수 있습니다.", HttpStatus.CONFLICT);

    private final String status;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.builder()
                .status(status)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
