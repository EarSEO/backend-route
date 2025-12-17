package com.earseo.route.service.route;

import org.locationtech.jts.geom.LineString;

import java.util.List;

public record RouteSearchResult(
        List<RoutePathPoint> path,
        List<RouteSearchItem> items,
        LineString lineString
) {
}
