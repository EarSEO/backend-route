package com.earseo.route.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "완료된 경로 리스트 요약 정보")
public record CompletedRouteSummaryResponse(
        @Schema(description = "경로 ID", example = "12")
        Long routeId,

        @Schema(description = "경로 이름", example = "경복궁-종묘")
        String name,

        @Schema(description = "완료 날짜 (yyyy.MM.dd)", example = "2025.12.10")
        String completedAt
) {
}
