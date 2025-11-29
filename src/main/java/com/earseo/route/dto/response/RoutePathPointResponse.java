package com.earseo.route.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경로 상의 좌표 한 점의 정보")
public record RoutePathPointResponse(

        @Schema(description = "경도", example = "127.9768")
        Double longitude,

        @Schema(description = "위도", example = "37.5759")
        Double latitude
) {}
