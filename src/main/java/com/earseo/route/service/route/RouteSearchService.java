package com.earseo.route.service.route;

import com.earseo.route.dto.response.SightMetaResponse;

import java.util.List;

public interface RouteSearchService {
    RouteSearchResult findRoute(Long memberId, List<SightMetaResponse> sights);
}
