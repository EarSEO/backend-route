package com.earseo.route.service;

import com.earseo.route.controller.client.SightFeignClient;
import com.earseo.route.dto.request.CreateRouteRequest;
import com.earseo.route.dto.response.InProgressRouteDetailResponse;
import com.earseo.route.dto.response.RouteItemResponse;
import com.earseo.route.dto.response.RoutePathPointResponse;
import com.earseo.route.dto.response.SightMetaResponse;
import com.earseo.route.entity.Route;
import com.earseo.route.entity.RouteItem;
import com.earseo.route.entity.RouteStatus;
import com.earseo.route.repository.RouteItemRepository;
import com.earseo.route.repository.RouteRepository;
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
    private final RouteRepository routeRepository;
    private final RouteItemRepository routeItemRepository;

    public InProgressRouteDetailResponse createInProgressRoute(Long memberId, CreateRouteRequest request) {

        if (request.placeIds() == null || request.placeIds().isEmpty()) {
            /** todo : 예외 처리 task 에서 수정
             */
            throw new IllegalArgumentException("최소 1개 이상의 관광지 ID가 필요합니다.");
        }

        List<SightMetaResponse> sights = sightFeignClient.getSightByIds(request.placeIds());

        RouteSearchResult searchResult = routeSearchService.findRoute(memberId, sights);

        String routeName = buildRouteName(searchResult);
        Route route = Route.createInProgress(memberId, routeName);

        List<RouteItem> routeItems = searchResult.items().stream().map(item -> RouteItem.of(
                item.type(),
                item.refId(),
                item.name(),
                item.imageUrl(),
                item.address(),
                item.latitude(),
                item.longitude(),
                item.docentUrl(),
                item.theme()
        )).toList();

        route.addItems(routeItems);
        Route savedRoute = routeRepository.save(route);

        List<RoutePathPointResponse> pathResponses = searchResult.path().stream().map(RouteInProgressService::toPathResponse).toList();

        List<RouteItemResponse> itemResponses = routeItems.stream().map(item -> new RouteItemResponse(
                item.getRefType(),
                item.getRefId(),
                item.getName(),
                item.getImageUrl(),
                item.getAddress(),
                new RoutePathPointResponse(item.getLongitude(), item.getLatitude()),
                item.getDocentUrl(),
                item.getTheme()
        )).toList();

        return new InProgressRouteDetailResponse(
                savedRoute.getId(),
                pathResponses,
                itemResponses
        );
    }

    public void completeRoute(Long memberId, Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() ->
                        /** todo : 예외 처리 task 에서 수정
                         */
                        new IllegalArgumentException("존재하지 않는 경로입니다.")
                );

        if (!route.getMemberId().equals(memberId)) {
            /** todo : 예외 처리 task 에서 수정
             */
            throw new IllegalArgumentException("해당 사용자의 경로가 아닙니다.");
        }

        if (route.getStatus() != RouteStatus.IN_PROGRESS) {
            /** todo : 예외 처리 task 에서 수정
             */
            throw new IllegalArgumentException("진행 중이 아닌 경로는 완료할 수 없습니다.");
        }

        route.complete();
    }

    private static RoutePathPointResponse toPathResponse(RoutePathPoint p) {
        return new RoutePathPointResponse(p.longitude(), p.latitude());
    }

    private String buildRouteName(RouteSearchResult searchResult) {
        if (searchResult.items().isEmpty()) {
            return "나의 경로";
        }

        String start = searchResult.items().get(0).name();
        String end = searchResult.items().get(searchResult.items().size() - 1).name();

        if (start.equals(end)) {
            return start;
        }

        return start + " - " + end;
    }
}
