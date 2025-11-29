package com.earseo.route.service;

import com.earseo.route.controller.client.SightFeignClient;
import com.earseo.route.dto.request.CreateRouteRequest;
import com.earseo.route.dto.response.InProgressRouteDetailResponse;
import com.earseo.route.dto.response.RouteItemResponse;
import com.earseo.route.dto.response.RoutePathPointResponse;
import com.earseo.route.dto.response.SightMetaResponse;
import com.earseo.route.service.route.RoutePathPoint;
import com.earseo.route.service.route.RouteSearchResult;
import com.earseo.route.service.route.RouteSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteInProgressService {

    private final SightFeignClient sightFeignClient;
    private final RouteSearchService routeSearchService;

    public InProgressRouteDetailResponse createInProgressRoute(Long memberId, CreateRouteRequest request) {

        /** 내부 처리 흐름 확인용
         * 1. 요청 검증
         * 2. 관광지 메타 조회
         * 3. 경로 찾기(호출만)
         * 4. path 변환
         * 5. items -> RouteItemResponse 변환
         * 6. db 저장 없이 응답 조립만
         **/

        if (request.placeIds() == null || request.placeIds().isEmpty()) {
            /** todo : 예외 처리 task 에서 수정
             */
            throw new IllegalArgumentException("최소 1개 이상의 관광지 ID가 필요합니다.");
        }

        List<SightMetaResponse> sights = sightFeignClient.getSightByIds(request.placeIds());

        RouteSearchResult searchResult = routeSearchService.findRoute(memberId, sights);

        List<RoutePathPointResponse> pathResponses = searchResult.path().stream().map(RouteInProgressService::toPathResponse).toList();

        List<RouteItemResponse> itemResponses = searchResult.items().stream().map(item -> new RouteItemResponse(
                item.type(),
                item.refId(),
                item.name(),
                item.imageUrl(),
                item.address(),
                new RoutePathPointResponse(item.longitude(), item.latitude()),
                item.docentUrl(),
                item.theme()
        )).toList();

        return new InProgressRouteDetailResponse(
                null,
                pathResponses,
                itemResponses
        );
    }

    private static RoutePathPointResponse toPathResponse(RoutePathPoint p) {
        return new RoutePathPointResponse(p.longitude(), p.latitude());
    }
}
