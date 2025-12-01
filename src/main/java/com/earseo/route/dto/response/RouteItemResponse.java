package com.earseo.route.dto.response;

import com.earseo.route.entity.RouteRefType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경로를 구성하는 개별 지점(SIGHT / STORY_SPOT)의 정보")
public record RouteItemResponse(

        @Schema(description = "아이템 타입(SIGHT / STORY_SPOT)", example = "SIGHT")
        RouteRefType itemType,

        @Schema(description = "참조 ID(관광지 / 스토리 스팟의 ID", example = "12")
        String itemId,

        @Schema(description = "노출용 이름", example = "경복궁")
        String itemName,

        @Schema(description = "대표 이미지 URL", example = "https://cdn.example.com/image/spot/1.jpg")
        String itemImageUrl,

        @Schema(description = "주소", example = "서울 종로구")
        String itemAddress,

        @Schema(description = "위치 정보")
        RoutePathPointResponse point,

        @Schema(description = "도슨트 오디오 URL", example = "https://cdn.example.com/docent/spot/1-ko.mp3")
        String itemDocentUrl,

        @Schema(description = "주요 테마", example = "A01")
        String itemMajorTheme,

        @Schema(description = "이야기 요약 아이디", example = "1L")
        Long summaryId
) {}
