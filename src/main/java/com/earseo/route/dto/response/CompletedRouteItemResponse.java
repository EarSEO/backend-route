package com.earseo.route.dto.response;


import com.earseo.route.entity.RouteRefType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "완료된 경로를 구성하는 개별 지점(SIGHT / STORY_SPOT)의 정보")
public record CompletedRouteItemResponse(
        @Schema(description = "아이템 타입(SIGHT / STORY_SPOT)", example = "SIGHT")
        RouteRefType itemType,

        @Schema(description = "참조 ID(관광지 / 스토리 스팟의 ID", example = "12")
        String itemId,

        @Schema(description = "노출용 이름", example = "경복궁")
        String itemName,

        @Schema(description = "대표 이미지 URL", example = "https://cdn.example.com/image/spot/1.jpg")
        String itemImageUrl
) {
}
