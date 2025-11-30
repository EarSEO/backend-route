package com.earseo.route.controller.client;

import com.earseo.route.dto.response.SightMetaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "sight-service",
        url = "${feign.sight-service.url}"
)
public interface SightFeignClient {

    @GetMapping("/internal/sight/meta")
    List<SightMetaResponse> getSightByIds(@RequestParam("ids") List<String> ids);
}
