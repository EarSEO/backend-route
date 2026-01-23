package com.earseo.route.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "완료된 경로 수정 요청 DTO")
public record ModifyCompletedRouteRequest(
        @NotBlank(message = "경로의 이름은 필수입니다")
        @Schema(description = "변경할 완료된 경로 이름", example = "나의 여행 1")
        String name

) {}
