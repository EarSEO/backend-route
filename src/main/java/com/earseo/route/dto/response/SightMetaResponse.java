package com.earseo.route.dto.response;

public record SightMetaResponse(
        String id,
        String name,
        String address,
        String imageUrl,
        Double latitude,
        Double longitude,
        String docentUrl,
        String theme
) {}
