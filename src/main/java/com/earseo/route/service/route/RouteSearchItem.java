package com.earseo.route.service.route;

import com.earseo.route.entity.RouteRefType;

public record RouteSearchItem(
        RouteRefType type,
        String refId,
        String name,
        String imageUrl,
        String address,
        Double latitude,
        Double longitude,
        String docentUrl,
        String theme
) {
}
