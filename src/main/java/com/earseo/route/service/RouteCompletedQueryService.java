package com.earseo.route.service;

import com.earseo.route.common.exception.BaseException;
import com.earseo.route.common.exception.RouteError;
import com.earseo.route.dto.request.ModifyCompletedRouteRequest;
import com.earseo.route.dto.response.CompletedRouteDetailResponse;
import com.earseo.route.dto.response.CompletedRouteItemResponse;
import com.earseo.route.dto.response.CompletedRouteListResponse;
import com.earseo.route.dto.response.CompletedRouteSummaryResponse;
import com.earseo.route.entity.Route;
import com.earseo.route.entity.RouteItem;
import com.earseo.route.entity.RouteStatus;
import com.earseo.route.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteCompletedQueryService {

    private static final int DEFAULT_PAGE_SIZE = 15;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final RouteRepository routeRepository;

    public CompletedRouteListResponse getCompletedRoutes(Long memberId, Pageable pageable) {
        Slice<Route> slice = routeRepository.findByMemberIdAndStatusOrderByCreatedAtDesc(
                memberId,
                RouteStatus.COMPLETED,
                pageable
        );

        List<CompletedRouteSummaryResponse> routes = slice.getContent().stream().map(this::toResponse).toList();

        return new CompletedRouteListResponse(routes, slice.hasNext());
    }

    private CompletedRouteSummaryResponse toResponse(Route route) {
        return new CompletedRouteSummaryResponse(
                route.getId(),
                route.getName(),
                route.getCreatedAt().format(DATE_FORMATTER)
        );
    }

    private CompletedRouteItemResponse toRouteItemResponse(RouteItem routeItem) {
        return new CompletedRouteItemResponse(
                routeItem.getRefType(),
                routeItem.getRefId(),
                routeItem.getName(),
                routeItem.getImageUrl()
        );
    }

    @Transactional
    public CompletedRouteDetailResponse getCompletedRouteDetail(Long memberId, Long routeId) {
        Route route = routeRepository.findCompletedRouteDetail(routeId, memberId)
                .orElseThrow(() -> new BaseException(RouteError.ROUTE_NOT_FOUND));

        List<RouteItem> routeItems = route.getItems();

        return new CompletedRouteDetailResponse(toResponse(route), routeItems.stream().map(this::toRouteItemResponse).toList());
    }

    @Transactional
    public CompletedRouteSummaryResponse modifyCompletedRouteName(ModifyCompletedRouteRequest modifyCompletedRoute, Long memberId, Long routeId) {
        Route route = routeRepository.findCompletedRouteDetail(memberId, routeId)
                .orElseThrow(() -> new BaseException(RouteError.ROUTE_NOT_FOUND));

        route.modifyName(modifyCompletedRoute.name());
        routeRepository.save(route);

        return new CompletedRouteSummaryResponse(route.getId(), route.getName(), route.getCreatedAt().format(DATE_FORMATTER));
    }

    @Transactional
    public void deleteRoute(Long routeId, Long memberId) {
        Route route = routeRepository.findCompletedRouteDetail(memberId, routeId)
                .orElseThrow(() -> new BaseException(RouteError.ROUTE_NOT_FOUND));

        routeRepository.delete(route);
    }
}
