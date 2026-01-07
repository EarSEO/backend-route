package com.earseo.route.service.route;

import com.earseo.route.dto.response.SightMetaResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import org.locationtech.jts.geom.LineString;

/** 테스트용! 기능 개발 완료 후 제거 예정
 * - 테스트 통과 & 컴파일용 스텁(Stub)
 * - API 플로우 검증용
 */

@Service
public class DummyRouteSearchService implements RouteSearchService {

    @Override
    public RouteSearchResult findRoute(Long memberId, List<SightMetaResponse> sights) {
        return new RouteSearchResult(
                Collections.<RoutePathPoint>emptyList(),
                Collections.<RouteSearchItem>emptyList(),
                (LineString) null
        );
    }
}
