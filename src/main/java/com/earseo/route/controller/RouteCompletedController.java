package com.earseo.route.controller;

import com.earseo.route.common.BaseResponse;
import com.earseo.route.dto.response.CompletedRouteDetailResponse;
import com.earseo.route.dto.response.CompletedRouteListResponse;
import com.earseo.route.service.RouteCompletedQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/route")
@RequiredArgsConstructor
public class RouteCompletedController {

    private final RouteCompletedQueryService routeCompletedQueryService;

    @Operation(
            summary = "[완료된 경로 기록 관리] 완료된 경로 리스트 조회",
            description = """
                    정상 종료(COMPLETED)된 경로를 최신 생성순(createdAt DESC)으로 조회합니다.
                    무한 스크롤을 위해 page/size 기반 페이징을 사용합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "완료된 경로 리스트 조회 성공",
                    content = @Content(schema = @Schema(implementation = CompletedRouteListResponse.class))
            )
    })
    @GetMapping("/completed")
    public ResponseEntity<BaseResponse<CompletedRouteListResponse>> getCompletedRoutes (@RequestHeader("X-USER-ID") Long userId,
                                                                                        @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(routeCompletedQueryService.getCompletedRoutes(userId, pageable)));
    }

    @Operation(
            summary = "[완료된 경로 기록 관리] 완료된 경로 상세정보 조회",
            description = """
                    완료된 경로의 상세정보를 사용자 ID, 완료된 경로의 ID 기반으로 조회합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "완료된 경로 상세정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = CompletedRouteDetailResponse.class))
            )
    })
    @GetMapping("/completed/detail/{routeId}")
    public ResponseEntity<BaseResponse<CompletedRouteDetailResponse>> getCompletedRouteDetail(@RequestHeader("X-USER-ID") Long userId,
                                                                                              @PathVariable("routeId") Long routeId) {
        return ResponseEntity.ok(BaseResponse.ok(routeCompletedQueryService.getCompletedRouteDetail(userId, routeId)));
    }
}
