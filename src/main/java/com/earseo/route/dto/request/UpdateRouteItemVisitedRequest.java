package com.earseo.route.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.codehaus.commons.nullanalysis.NotNull;

@Schema(description = "RouteItem 방문(리스닝) 상태 변경 요청")
public record UpdateRouteItemVisitedRequest(
        @NotNull
        @Schema(description = "방문(리스닝) 완료 여부", example = "true")
        Boolean visited
) {}
