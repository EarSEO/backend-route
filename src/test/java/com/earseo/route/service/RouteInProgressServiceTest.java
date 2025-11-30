package com.earseo.route.service;

import com.earseo.route.common.exception.BaseException;
import com.earseo.route.common.exception.RouteError;
import com.earseo.route.controller.client.SightFeignClient;
import com.earseo.route.dto.request.CreateRouteRequest;
import com.earseo.route.dto.response.InProgressRouteDetailResponse;
import com.earseo.route.dto.response.SightMetaResponse;
import com.earseo.route.entity.Route;
import com.earseo.route.entity.RouteRefType;
import com.earseo.route.entity.RouteStatus;
import com.earseo.route.repository.RouteItemRepository;
import com.earseo.route.repository.RouteRepository;
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
import java.util.Optional;

import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
class RouteInProgressServiceTest {

    @Mock
    private SightFeignClient sightFeignClient;

    @Mock
    private RouteSearchService routeSearchService;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteItemRepository routeItemRepository;

    @InjectMocks
    private RouteInProgressService routeInProgressService;

    @Test
    @DisplayName("placeIds가 비어 있으면 예외 발생")
    void createInProgressRoute_빈ID리스트_예외() {
        //given
        CreateRouteRequest req = new CreateRouteRequest(List.of());

        // when
        BaseException ex = Assertions.catchThrowableOfType(
                () -> routeInProgressService.createInProgressRoute(1L, req),
                BaseException.class
        );

        // then
        Assertions.assertThat(ex.getErrorCode().getStatus()).isEqualTo(RouteError.INVALID_PLACE_IDS.getStatus());
        Assertions.assertThat(ex.getErrorCode().getMessage()).isEqualTo(RouteError.INVALID_PLACE_IDS.getMessage());
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

        Mockito.when(routeRepository.save(Mockito.any(Route.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
        Mockito.verify(routeRepository, Mockito.times(1)).save(Mockito.any(Route.class));
    }

    @Test
    @DisplayName("경로 정상 종료 - IN_PROGRESS → COMPLETED")
    void completeRoute_성공() {
        //given
        long memberId = 1L;
        long routeId = 100L;

        Route route = Mockito.mock(Route.class);
        Mockito.when(route.getMemberId()).thenReturn(memberId);
        Mockito.when(route.getStatus()).thenReturn(RouteStatus.IN_PROGRESS);

        Mockito.when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        //when
        routeInProgressService.completeRoute(memberId, routeId);

        //then
        Mockito.verify(route, Mockito.times(1)).complete();
    }

    @Test
    @DisplayName("경로 정상 종료 - 소유자가 아니면 예외")
    void completeRoute_소유자아님_예외() {
        //given
        Long memberId = 1L;
        Long routeId = 100L;

        Route route = Mockito.mock(Route.class);
        Mockito.when(route.getMemberId()).thenReturn(999L);

        Mockito.when(routeRepository.findById(routeId))
                .thenReturn(Optional.of(route));

        //when
        BaseException ex = Assertions.catchThrowableOfType(
                () -> routeInProgressService.completeRoute(memberId, routeId),
                BaseException.class
        );

        //then
        Assertions.assertThat(ex.getErrorCode().getStatus()).isEqualTo(RouteError.ROUTE_NOT_OWNER.getStatus());
        Mockito.verify(route, Mockito.never()).complete();
    }

    @Test
    @DisplayName("경로 정상 종료 - IN_PROGRESS가 아니면 예외")
    void completeRoute_진행중아님_예외() {
        // given
        Long memberId = 1L;
        Long routeId = 100L;

        Route route = Mockito.mock(Route.class);
        Mockito.when(route.getMemberId()).thenReturn(memberId);
        Mockito.when(route.getStatus()).thenReturn(RouteStatus.COMPLETED);

        Mockito.when(routeRepository.findById(routeId))
                .thenReturn(Optional.of(route));

        // when
        BaseException ex = Assertions.catchThrowableOfType(
                () -> routeInProgressService.completeRoute(memberId, routeId),
                BaseException.class
        );

        // then
        Assertions.assertThat(ex.getErrorCode().getStatus()).isEqualTo(RouteError.ROUTE_NOT_IN_PROGRESS.getStatus());
        Mockito.verify(route, Mockito.never()).complete();
    }

}