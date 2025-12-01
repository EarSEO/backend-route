package com.earseo.route.service;

import com.earseo.route.common.exception.BaseException;
import com.earseo.route.common.exception.RouteError;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteInProgressService {

    private final SightFeignClient sightFeignClient;
//    private final RouteSearchService routeSearchService;
    private final RouteSearchService routeSearchService;
    private final RouteRepository routeRepository;
    private final RouteItemRepository routeItemRepository;

    public InProgressRouteDetailResponse createInProgressRoute(Long memberId, CreateRouteRequest request) {

        if (request.placeIds() == null || request.placeIds().isEmpty()) {
            throw new BaseException(RouteError.INVALID_PLACE_IDS);
        }

        routeRepository.findByMemberIdAndStatus(memberId, RouteStatus.IN_PROGRESS)
                .ifPresent(routeRepository::delete);

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

        List<RoutePathPointResponse> pathResponses = searchResult.path().stream()
                .map(p -> new RoutePathPointResponse(p.longitude(), p.latitude()))
                .toList();

        List<RouteItemResponse> itemResponses = searchResult.items().stream().map(item -> new RouteItemResponse(
                item.type(),
                item.refId(),
                item.name(),
                item.imageUrl(),
                item.address(),
                new RoutePathPointResponse(item.longitude(), item.latitude()),
                item.docentUrl(),
                item.theme(),
                item.summaryId()
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
                        new BaseException(RouteError.ROUTE_NOT_FOUND));

        if (!route.getMemberId().equals(memberId)) {
            throw new BaseException(RouteError.ROUTE_NOT_OWNER);
        }

        if (route.getStatus() != RouteStatus.IN_PROGRESS) {
            throw new BaseException(RouteError.ROUTE_NOT_IN_PROGRESS);
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
