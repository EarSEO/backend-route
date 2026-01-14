package com.earseo.route.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "완료된 경로 리스트 응답")
public record CompletedRouteListResponse(
        @Schema(description = "완료된 경로 리스트")
        List<CompletedRouteSummaryResponse> routes,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {
}
