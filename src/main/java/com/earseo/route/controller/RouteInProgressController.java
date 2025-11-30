package com.earseo.route.controller;

import com.earseo.route.common.BaseResponse;
import com.earseo.route.dto.request.CreateRouteRequest;
import com.earseo.route.dto.response.InProgressRouteDetailResponse;
import com.earseo.route.service.RouteInProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/route")
@RequiredArgsConstructor
public class RouteInProgressController {

    private final RouteInProgressService routeInProgressService;

    @Operation(
            summary = "[내 경로 진행 관리] 진행 중 경로 생성 및 조회",
            description = """
                    사용자가 선택한 관광지 ID 리스트(placeIds)와 X-USER-ID를 기반으로
                    진행 중 경로를 생성/조회합니다.
                    - 관광지 메타 정보는 sight-service에서 조회하고,
                    - sight 사이에 story_spot을 삽입하는 경로 계산은 별도 도메인 서비스에서 처리합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "진행 중 경로 조회 성공",
                    content = @Content(schema = @Schema(implementation = InProgressRouteDetailResponse.class))
            )
    })
    @PostMapping("/in-progress")
    public ResponseEntity<BaseResponse<InProgressRouteDetailResponse>> createInProgressRoute(@RequestHeader("X-USER-ID") Long userId, @RequestBody CreateRouteRequest request) {
        return ResponseEntity.ok(BaseResponse.ok(routeInProgressService.createInProgressRoute(userId, request)));
    }

    @Operation(
            summary = "[내 경로 진행 관리] 진행 중 경로 정상 종료",
            description = "진행 중(IN_PROGRESS) 상태의 경로를 완료(COMPLETED) 상태로 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "경로 종료 처리 성공"
            )
    })
    @PostMapping("/{routeId}/complete")
    public ResponseEntity<BaseResponse<Void>> completeRoute(@RequestHeader("X-USER-ID") Long userId, @PathVariable("routeId") Long routeId) {
        routeInProgressService.completeRoute(userId, routeId);
        return ResponseEntity.ok(BaseResponse.ok(null));
    }
}
