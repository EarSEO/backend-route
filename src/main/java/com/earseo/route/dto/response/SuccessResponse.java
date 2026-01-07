package com.earseo.route.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "성공 여부 응답")
public record SuccessResponse(
        @Schema(description = "성공 여부", example = "true")
        boolean isSuccess
) {
    public static SuccessResponse ok() {
        return new SuccessResponse(true);
    }
}
