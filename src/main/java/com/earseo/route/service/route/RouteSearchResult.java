package com.earseo.route.service.route;

import java.util.List;

public record RouteSearchResult(
        List<RoutePathPoint> path,
        List<RouteSearchItem> items
) {
}
