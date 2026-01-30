package com.earseo.route.service;

import com.earseo.route.common.exception.BaseException;
import com.earseo.route.common.exception.RouteError;
import com.earseo.route.controller.client.SightFeignClient;
import com.earseo.route.dto.request.CreateRouteRequest;
import com.earseo.route.dto.response.InProgressRouteDetailResponse;
import com.earseo.route.dto.response.RouteItemResponse;
import com.earseo.route.dto.response.SightMetaResponse;
import com.earseo.route.entity.Route;
import com.earseo.route.entity.RouteItem;
import com.earseo.route.entity.RouteRefType;
import com.earseo.route.entity.RouteStatus;
import com.earseo.route.repository.RouteItemRepository;
import com.earseo.route.repository.RouteRepository;
import com.earseo.route.service.route.RoutePathPoint;
import com.earseo.route.service.route.RouteSearchItem;
import com.earseo.route.service.route.RouteSearchResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
    @DisplayName("createInProgressRoute: placeIds 비어있으면 INVALID_PLACE_IDS")
    void createInProgressRoute_빈ID리스트_예외() {
        // given
        CreateRouteRequest req = new CreateRouteRequest(List.of(), null);

        // when
        BaseException ex = Assertions.catchThrowableOfType(
                () -> routeInProgressService.createInProgressRoute(1L, req),
                BaseException.class
        );

        // then
        Assertions.assertThat(ex.getErrorCode().getStatus())
                .isEqualTo(RouteError.INVALID_PLACE_IDS.getStatus());
    }

    @Test
    @DisplayName("createInProgressRoute: 정상 - 기존 IN_PROGRESS 삭제 → sight 메타 조회 → findRoute → 저장 → 응답 매핑")
    void createInProgressRoute_성공() {
        //given
        long memberId = 10L;
        List<String> placeIds = List.of("1","4");
        CreateRouteRequest req = new CreateRouteRequest(placeIds, null);

        Route existing = Mockito.mock(Route.class);
        Mockito.when(routeRepository.findByMemberIdAndStatus(memberId, RouteStatus.IN_PROGRESS))
                .thenReturn(Optional.of(existing));

        List<SightMetaResponse> mockedSights = List.of(
                new SightMetaResponse(
                        "1",
                        "경복궁",
                        "서울 종로구",
                        "image1",
                        37.57,
                        126.97,
                        "docent1",
                        "A01"),
                new SightMetaResponse(
                        "4",
                        "DDP",
                        "서울 중구",
                        "image2",
                        37.56,
                        127.00,
                        "docent2",
                        "A02")
        );
        Mockito.when(sightFeignClient.getSightByIds(placeIds)).thenReturn(mockedSights);

        GeometryFactory gf = new GeometryFactory();
        LineString lineString = gf.createLineString(new Coordinate[]{
                new Coordinate(126.97, 37.57),
                new Coordinate(127.00, 37.56)
        });

        RouteSearchResult mockedResult = new RouteSearchResult(
                List.of(
                        new RoutePathPoint(126.97, 37.57),
                        new RoutePathPoint(127.00, 37.56)
                ),
                List.of(
                        new RouteSearchItem(
                                RouteRefType.SIGHT,
                                "1",
                                "경복궁",
                                "img1",
                                "서울 종로구",
                                37.57,
                                126.97,
                                "docent1",
                                "A01",
                                null),
                        new RouteSearchItem(
                                RouteRefType.STORY_SPOT,
                                "999",
                                "스토리 스팟",
                                null,
                                null,
                                37.565,
                                126.985,
                                null,
                                null,
                                123L)
                ),
                lineString
        );

        Mockito.when(routeSearchService.findRoute(memberId, mockedSights, req.point()))
                .thenReturn(mockedResult);

        Route savedRoute = Mockito.mock(Route.class);
        Mockito.when(savedRoute.getId()).thenReturn(1L);
        Mockito.when(routeRepository.save(Mockito.any(Route.class))).thenReturn(savedRoute);

        // when
        InProgressRouteDetailResponse res = routeInProgressService.createInProgressRoute(memberId, req);

        // then
        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.routeId()).isEqualTo(1L);

        Assertions.assertThat(res.path()).hasSize(2);
        Assertions.assertThat(res.routeItems()).hasSize(2);

        RouteItemResponse second = res.routeItems().get(1);
        Assertions.assertThat(second.itemType()).isEqualTo(RouteRefType.STORY_SPOT);
        Assertions.assertThat(second.summaryId()).isEqualTo(123L);
        Assertions.assertThat(second.visited()).isFalse();

        // verify
        Mockito.verify(routeRepository, Mockito.times(1))
                .findByMemberIdAndStatus(memberId, RouteStatus.IN_PROGRESS);
        Mockito.verify(routeRepository, Mockito.times(1))
                .delete(existing);
        Mockito.verify(sightFeignClient, Mockito.times(1))
                .getSightByIds(placeIds);
        Mockito.verify(routeSearchService, Mockito.times(1))
                .findRoute(memberId, mockedSights, req.point());
        Mockito.verify(routeRepository, Mockito.times(1))
                .save(Mockito.any(Route.class));
    }

    @Test
    @DisplayName("getInProgressRoute: 진행 중 경로 없으면 routeId=null, path=[], routeItems=[] 반환")
    void getInProgressRoute_없음_빈응답() {
        // given
        long memberId = 10L;

        Mockito.when(routeRepository.findWithItemsByMemberIdAndStatus(memberId, RouteStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());

        // when
        InProgressRouteDetailResponse res = routeInProgressService.getInProgressRoute(memberId);

        // then
        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.routeId()).isNull();
        Assertions.assertThat(res.path()).isEmpty();
        Assertions.assertThat(res.routeItems()).isEmpty();
    }

    @Test
    @DisplayName("getInProgressRoute: 진행 중 경로 있으면 path(LineString) + items(visited 포함) 반환")
    void getInProgressRoute_성공() {
        // given
        long memberId = 10L;

        Route route = Mockito.mock(Route.class);

        GeometryFactory gf = new GeometryFactory();
        LineString lineString = gf.createLineString(new Coordinate[]{
                new Coordinate(126.97, 37.57),
                new Coordinate(127.00, 37.56)
        });

        Mockito.when(route.getId()).thenReturn(777L);
        Mockito.when(route.getPath()).thenReturn(lineString);

        RouteItem item1 = Mockito.mock(RouteItem.class);
        Mockito.when(item1.getRefType()).thenReturn(RouteRefType.SIGHT);
        Mockito.when(item1.getRefId()).thenReturn("1");
        Mockito.when(item1.getName()).thenReturn("경복궁");
        Mockito.when(item1.getImageUrl()).thenReturn("img");
        Mockito.when(item1.getAddress()).thenReturn("addr");
        Mockito.when(item1.getLongitude()).thenReturn(126.97);
        Mockito.when(item1.getLatitude()).thenReturn(37.57);
        Mockito.when(item1.getDocentUrl()).thenReturn("docent");
        Mockito.when(item1.getTheme()).thenReturn("A01");
        Mockito.when(item1.getSummaryId()).thenReturn(null);
        Mockito.when(item1.isVisited()).thenReturn(true);

        Mockito.when(route.getItems()).thenReturn(List.of(item1));

        Mockito.when(routeRepository.findWithItemsByMemberIdAndStatus(memberId, RouteStatus.IN_PROGRESS))
                .thenReturn(Optional.of(route));

        // when
        InProgressRouteDetailResponse res = routeInProgressService.getInProgressRoute(memberId);

        // then
        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.routeId()).isEqualTo(777L);

        Assertions.assertThat(res.path()).hasSize(2);
        Assertions.assertThat(res.path().getFirst().longitude()).isEqualTo(126.97);
        Assertions.assertThat(res.path().getFirst().latitude()).isEqualTo(37.57);

        Assertions.assertThat(res.routeItems()).hasSize(1);
        Assertions.assertThat(res.routeItems().getFirst().visited()).isTrue();
    }


    @Test
    @DisplayName("updateRouteItemVisited: 성공 - IN_PROGRESS이면 markVisited 호출")
    void updateRouteItemVisited_성공() {
        // given
        long memberId = 10L;
        long routeItemId = 55L;

        RouteItem routeItem = Mockito.mock(RouteItem.class);

        Mockito.when(routeItemRepository.findWithRouteByIdAndMemberIdAndRouteStatus(
                        routeItemId, memberId, RouteStatus.IN_PROGRESS))
                .thenReturn(Optional.of(routeItem));

        // when
        routeInProgressService.updateRouteItemVisited(memberId, routeItemId, true);

        // then
        Mockito.verify(routeItem, Mockito.times(1)).markVisited(true);
    }

    @Test
    @DisplayName("updateRouteItemVisited: 실패 - 조건 불일치(없음/소유자아님/진행중아님) -> ROUTE_ITEM_NOT_IN_PROGRESS")
    void updateRouteItemVisited_실패() {
        // given
        long memberId = 10L;
        long routeItemId = 55L;

        Mockito.when(routeItemRepository.findWithRouteByIdAndMemberIdAndRouteStatus(
                        routeItemId, memberId, RouteStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());

        // when
        BaseException ex = Assertions.catchThrowableOfType(
                () -> routeInProgressService.updateRouteItemVisited(memberId, routeItemId, true),
                BaseException.class
        );

        // then
        Assertions.assertThat(ex.getErrorCode().getStatus())
                .isEqualTo(RouteError.ROUTE_ITEM_NOT_IN_PROGRESS.getStatus());
    }

    @Test
    @DisplayName("completeRoute: 경로 없음 -> ROUTE_NOT_FOUND")
    void completeRoute_경로없음_예외() {
        // given
        long memberId = 1L;
        long routeId = 100L;

        Mockito.when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        // when
        BaseException ex = Assertions.catchThrowableOfType(
                () -> routeInProgressService.completeRoute(memberId, routeId),
                BaseException.class
        );

        // then
        Assertions.assertThat(ex.getErrorCode().getStatus())
                .isEqualTo(RouteError.ROUTE_NOT_FOUND.getStatus());
    }

    @Test
    @DisplayName("completeRoute: 소유자 불일치 -> ROUTE_NOT_OWNER")
    void completeRoute_소유자아님_예외() {
        // given
        long memberId = 1L;
        long routeId = 100L;

        Route route = Mockito.mock(Route.class);
        Mockito.when(route.getMemberId()).thenReturn(999L);
        Mockito.when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        // when
        BaseException ex = Assertions.catchThrowableOfType(
                () -> routeInProgressService.completeRoute(memberId, routeId),
                BaseException.class
        );

        // then
        Assertions.assertThat(ex.getErrorCode().getStatus())
                .isEqualTo(RouteError.ROUTE_NOT_OWNER.getStatus());
        Mockito.verify(route, Mockito.never()).complete();
    }

    @Test
    @DisplayName("completeRoute: IN_PROGRESS 아님 -> ROUTE_NOT_IN_PROGRESS")
    void completeRoute_진행중아님_예외() {
        // given
        long memberId = 1L;
        long routeId = 100L;

        Route route = Mockito.mock(Route.class);
        Mockito.when(route.getMemberId()).thenReturn(memberId);
        Mockito.when(route.getStatus()).thenReturn(RouteStatus.COMPLETED);
        Mockito.when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        // when
        BaseException ex = Assertions.catchThrowableOfType(
                () -> routeInProgressService.completeRoute(memberId, routeId),
                BaseException.class
        );

        // then
        Assertions.assertThat(ex.getErrorCode().getStatus())
                .isEqualTo(RouteError.ROUTE_NOT_IN_PROGRESS.getStatus());
        Mockito.verify(route, Mockito.never()).complete();
    }

    @Test
    @DisplayName("completeRoute: 정상 - complete() 호출")
    void completeRoute_성공() {
        // given
        long memberId = 1L;
        long routeId = 100L;

        Route route = Mockito.mock(Route.class);
        Mockito.when(route.getMemberId()).thenReturn(memberId);
        Mockito.when(route.getStatus()).thenReturn(RouteStatus.IN_PROGRESS);
        Mockito.when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        // when
        routeInProgressService.completeRoute(memberId, routeId);

        // then
        Mockito.verify(route, Mockito.times(1)).complete();
    }
}