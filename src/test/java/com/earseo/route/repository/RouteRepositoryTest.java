//package com.earseo.route.repository;
//
//import com.earseo.route.entity.Route;
//import com.earseo.route.entity.RouteStatus;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.lang.reflect.Constructor;
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@DisplayName("Route 엔티티가 DB에 정상 저장되는지 확인한다")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ActiveProfiles("test")
//class RouteRepositoryTest {
//
//    @Autowired
//    private RouteRepository routeRepository;
//
//    @Test
//    void routeEntity_정상저장_DB연동확인() throws Exception {
//        // given
//        Route route = createRoute(
//                1L,
//                "테스트 경로",
//                RouteStatus.IN_PROGRESS
//        );
//
//        // when
//        Route saved = routeRepository.save(route);
//
//        // then
//        Route found = routeRepository.findById(saved.getId())
//                .orElseThrow();
//
//        assertThat(found.getId()).isNotNull();
//        assertThat(found.getMemberId()).isEqualTo(1L);
//        assertThat(found.getName()).isEqualTo("테스트 경로");
//        assertThat(found.getStatus()).isEqualTo(RouteStatus.IN_PROGRESS);
//    }
//
//    private Route createRoute(Long memberId, String name, RouteStatus status) throws Exception {
//        // protected 기본 생성자 강제로 호출
//        Constructor<Route> ctor = Route.class.getDeclaredConstructor();
//        ctor.setAccessible(true);
//        Route route = ctor.newInstance();
//
//        setField(route, "memberId", memberId);
//        setField(route, "name", name);
//        setField(route, "status", status);
//        setField(route, "startedAt", LocalDateTime.now());
//        setField(route, "createdAt", LocalDateTime.now());
//        setField(route, "updatedAt", LocalDateTime.now());
//
//        return route;
//    }
//
//    private void setField(Object target, String field, Object value) {
//        try {
//            var f = target.getClass().getDeclaredField(field);
//            f.setAccessible(true);
//            f.set(target, value);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}