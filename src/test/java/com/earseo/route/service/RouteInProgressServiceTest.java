package com.earseo.route.service;

import com.earseo.route.controller.client.SightFeignClient;
import com.earseo.route.dto.request.CreateRouteRequest;
import com.earseo.route.dto.response.InProgressRouteDetailResponse;
import com.earseo.route.dto.response.SightMetaResponse;
import com.earseo.route.entity.RouteRefType;
import com.earseo.route.service.route.RoutePathPoint;
import com.earseo.route.service.route.RouteSearchItem;
import com.earseo.route.service.route.RouteSearchResult;
import com.earseo.route.service.route.RouteSearchService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
class RouteInProgressServiceTest {

    @Mock
    private SightFeignClient sightFeignClient;

    @Mock
    private RouteSearchService routeSearchService;

    @InjectMocks
    private RouteInProgressService routeInProgressService;

    @Test
    @DisplayName("placeIds가 비어 있으면 예외 발생")
    void createInProgressRoute_빈ID리스트_예외() {
        //given
        CreateRouteRequest req = new CreateRouteRequest(List.of());

        //when & then
        Assertions.assertThatThrownBy(()-> routeInProgressService.createInProgressRoute(1L, req)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("최소 1개 이상의 관광지 ID가 필요합니다.");
    }

    @Test
    @DisplayName("정상 요청 시 - SIGHT 메타 조회 → RouteSearchService 호출 → 응답 반환")
    void createInProgressRoute_성공() {
        //given
        long memberId = 10L;
        List<String> placeIds = List.of("1","4");

        CreateRouteRequest req = new CreateRouteRequest(placeIds);

        // mock - SightFeignClient
        List<SightMetaResponse> mockedSights = List.of(
                new SightMetaResponse("1", "경복궁", "서울 종로구", "imageUrl1", 127.12334, 37.12344, "docentUrl1", "A01"),
                new SightMetaResponse("13", "DDP", "서울 중구", "imageUrl2", 127.14534, 37.17644, "docentUrl2", "A01")
        );
        Mockito.when(sightFeignClient.getSightByIds(placeIds)).thenReturn(mockedSights);

        // mock - RouteSearchService
        RouteSearchResult mockedResult = new RouteSearchResult(
                // path
                List.of(
                        new RoutePathPoint(126.92034, 37.53222),
                        new RoutePathPoint(126.24478, 37.23285)
                ),

                // items
                List.of(
                        new RouteSearchItem(RouteRefType.SIGHT, "1", "경복궁", "img1", "서울 종로구",
                                126.97, 37.57, "docent1", "HISTORY"),
                        new RouteSearchItem(RouteRefType.STORY_SPOT, "4", "DDP", null, null,
                                126.98, 37.56, "docent2", null)
                )
        );
        Mockito.when(routeSearchService.findRoute(memberId, mockedSights)).thenReturn(mockedResult);

        //when
        InProgressRouteDetailResponse response = routeInProgressService.createInProgressRoute(memberId, req);

        //then
        Assertions.assertThat(response).isNotNull();

        // path 검증
        Assertions.assertThat(response.path()).hasSize(2);
        Assertions.assertThat(response.path().get(0).longitude()).isEqualTo(126.92034);
        Assertions.assertThat(response.path().get(0).latitude()).isEqualTo(37.53222);

        // items 검증
        Assertions.assertThat(response.routeItems()).hasSize(2);
        Assertions.assertThat(response.routeItems().get(0).itemId()).isEqualTo("1");
        Assertions.assertThat(response.routeItems().get(1).itemId()).isEqualTo("4");

        // verify mock
        Mockito.verify(sightFeignClient, Mockito.times(1)).getSightByIds(placeIds);
        Mockito.verify(routeSearchService, Mockito.times(1)).findRoute(memberId, mockedSights);
    }
}