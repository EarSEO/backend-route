package com.earseo.route.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "진행 중 경로 상세 응답")
public record InProgressRouteDetailResponse(

        @Schema(description = "경로 ID", example = "12")
        Long routeId,

        @Schema(description = "경로를 구성하는 라인 좌표 리스트")
        List<RoutePathPointResponse> path,

        @Schema(description = "경로를 구성하는 개별 지점(SIGHT / STORY_SPOT) 리스트")
        List<RouteItemResponse> routeItems
) {}
