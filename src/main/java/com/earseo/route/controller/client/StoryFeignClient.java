package com.earseo.route.controller.client;

import com.earseo.route.dto.request.GetRouteListSpotRequest;
import com.earseo.route.dto.response.GetRouteListSpotResponse;
import com.earseo.route.dto.response.SightMetaResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "story-service",
        url = "${feign.story-service.url}"
)
public interface StoryFeignClient {
    @PostMapping("internal/story/path/spots")
    GetRouteListSpotResponse getPathsSpotList(
            @RequestBody @Valid
            GetRouteListSpotRequest request
    );
}
