package com.earseo.route.service.route;

import com.earseo.route.dto.response.SightMetaResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DummyRouteSearchService implements RouteSearchService {

    @Override
    public RouteSearchResult findRoute(Long memberId, List<SightMetaResponse> sights) {
        /** todo: 경로 찾기 로직 구현 예정
         * 지금은 빈 결과를 반환해서 전체 플로우만 확인
         **/
        return new RouteSearchResult(
                Collections.emptyList(),
                Collections.emptyList()
        );
    }
}
