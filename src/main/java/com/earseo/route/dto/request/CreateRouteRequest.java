package com.earseo.route.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "경로 생성을 위한 요청 바디")
public record CreateRouteRequest(

        @Schema(
                description = "사용자가 선택한 관광지(sight)의 ID 리스트",
                example = "[\"1\", \"3\", \"12\"]"
        )
        List<String> placeIds
) {}
