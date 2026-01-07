package com.earseo.route.controller;

import com.earseo.route.common.BaseResponse;
import com.earseo.route.dto.request.CreateRouteRequest;
import com.earseo.route.dto.request.UpdateRouteItemVisitedRequest;
import com.earseo.route.dto.response.InProgressRouteDetailResponse;
import com.earseo.route.dto.response.SuccessResponse;
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
            summary = "[내 경로 진행 관리] 진행 중 경로 생성",
            description = "placeIds 기반으로 진행 중 경로를 새로 생성합니다. (기존 IN_PROGRESS가 있으면 삭제 후 재생성)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "진행 중 경로 생성 성공",
                    content = @Content(schema = @Schema(implementation = InProgressRouteDetailResponse.class))
            )
    })
    @PostMapping("/in-progress")
    public ResponseEntity<BaseResponse<InProgressRouteDetailResponse>> createInProgressRoute(@RequestHeader("X-USER-ID") Long userId, @RequestBody CreateRouteRequest request) {
        return ResponseEntity.ok(BaseResponse.ok(routeInProgressService.createInProgressRoute(userId, request)));
    }

    @Operation(
            summary = "[단일 로그인] 로그인 직후 진행 중 경로 복구 조회",
            description = """
                    로그인 완료 직후 호출하는 API입니다.
                    - 진행 중(IN_PROGRESS) 경로가 있으면 상세를 반환
                    - 없으면 data=null 반환 (에러 아님)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = InProgressRouteDetailResponse.class))
            )
    })
    @GetMapping("/in-progress")
    public ResponseEntity<BaseResponse<InProgressRouteDetailResponse>> getInProgressRoute(@RequestHeader("X-USER-ID") Long userId) {
        return ResponseEntity.ok(BaseResponse.ok(routeInProgressService.getInProgressRoute(userId)));
    }

    @Operation(
            summary = "[내 경로 진행 관리] 경로 아이템 방문(리스닝) 상태 저장",
            description = "진행 중(IN_PROGRESS) 경로의 특정 아이템(routeItemId)의 visited 상태를 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))
            )
    })
    @PatchMapping("/in-progress/items/{routeItemId}/visited")
    public ResponseEntity<BaseResponse<SuccessResponse>> updateVisited(@RequestHeader("X-USER-ID") Long userId, @PathVariable Long routeItemId, @RequestBody UpdateRouteItemVisitedRequest request) {
        routeInProgressService.updateRouteItemVisited(userId, routeItemId, request.visited());
        return ResponseEntity.ok(BaseResponse.ok(SuccessResponse.ok()));
    }

    @Operation(
            summary = "[내 경로 진행 관리] 진행 중 경로 정상 종료",
            description = "진행 중(IN_PROGRESS) 상태의 경로를 완료(COMPLETED) 상태로 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))
            )
    })
    @PostMapping("/{routeId}/complete")
    public ResponseEntity<BaseResponse<SuccessResponse>> completeRoute(@RequestHeader("X-USER-ID") Long userId, @PathVariable("routeId") Long routeId) {
        routeInProgressService.completeRoute(userId, routeId);
        return ResponseEntity.ok(BaseResponse.ok(SuccessResponse.ok()));
    }
}
