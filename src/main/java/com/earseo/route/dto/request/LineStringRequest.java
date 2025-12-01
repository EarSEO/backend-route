package com.earseo.route.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "경로를 구성하는 점들의 배열")
public record LineStringRequest(
    @Schema(
        description = "경로를 구성하는 좌표 목록 (최소 2개 이상)",
        minLength = 2,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    List<PointRequest> lineString
) {
}
