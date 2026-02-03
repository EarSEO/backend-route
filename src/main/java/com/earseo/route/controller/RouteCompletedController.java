package com.earseo.route.controller;

import com.earseo.route.common.BaseResponse;
import com.earseo.route.dto.request.ModifyCompletedRouteRequest;
import com.earseo.route.dto.response.CompletedRouteDetailResponse;
import com.earseo.route.dto.response.CompletedRouteListResponse;
import com.earseo.route.dto.response.CompletedRouteSummaryResponse;
import com.earseo.route.service.RouteCompletedQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/route")
@RequiredArgsConstructor
@Slf4j
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 정보와 완료된 경로 검증 실패 및 조회 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "RUT002",
                                                "message": "존재하지 않는 경로입니다.",
                                                "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/completed/detail/{routeId}")
    public ResponseEntity<BaseResponse<CompletedRouteDetailResponse>> getCompletedRouteDetail(@RequestHeader("X-USER-ID") Long userId,
                                                                                              @PathVariable("routeId") Long routeId) {
        log.info("memberId : {}", userId);
        log.info("routeId : {}", routeId);
        return ResponseEntity.ok(BaseResponse.ok(routeCompletedQueryService.getCompletedRouteDetail(userId, routeId)));
    }

    @Operation(
            summary = "[완료된 경로 기록 관리] 완료된 경로 이름 변경",
            description = """
                    완료된 경로의 이름을 변경합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "완료된 경로 이름 변경 성공",
                    content = @Content(schema = @Schema(implementation = CompletedRouteSummaryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 정보와 완료된 경로 검증 실패 및 조회 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "RUT002",
                                                "message": "존재하지 않는 경로입니다.",
                                                "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PutMapping("/completed/{routeId}")
    public ResponseEntity<BaseResponse<CompletedRouteSummaryResponse>> modifyCompletedRouteName(@Valid @RequestBody ModifyCompletedRouteRequest modifyCompletedRoute,
                                                                                                @RequestHeader("X-USER-ID") Long userId, @PathVariable("routeId") Long routeId) {
        return ResponseEntity.ok(BaseResponse.ok(routeCompletedQueryService.modifyCompletedRouteName(modifyCompletedRoute, userId, routeId)));
    }

    @Operation(
            summary = "[완료된 경로 기록 관리] 완료된 경로 삭제",
            description = """
                    완료된 경로를 삭제합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "완료된 경로 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 정보와 완료된 경로 검증 실패 및 조회 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "RUT002",
                                                "message": "존재하지 않는 경로입니다.",
                                                "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/completed/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable("routeId") Long routeId, @RequestHeader("X-USER-ID") Long userId) {
        routeCompletedQueryService.deleteRoute(routeId, userId);
        return ResponseEntity.noContent().build();
    }
}
