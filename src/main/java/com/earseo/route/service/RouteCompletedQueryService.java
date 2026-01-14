package com.earseo.route.service;

import com.earseo.route.dto.response.CompletedRouteListResponse;
import com.earseo.route.dto.response.CompletedRouteSummaryResponse;
import com.earseo.route.entity.Route;
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

    public CompletedRouteListResponse getCompletedRoutes(
            Long memberId,
            Pageable pageable
    ) {
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
}
