package com.earseo.route.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "완료된 경로 상세정보 응답")
public record CompletedRouteDetailResponse(
        @Schema(description = "완료된 경로 요약 정보")
        CompletedRouteSummaryResponse route,

        @Schema(description = "완료된 경로의 관광지 및 이야기 스팟 리스트")
        List<CompletedRouteItemResponse> items
) {
}
