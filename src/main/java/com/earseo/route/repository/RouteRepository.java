package com.earseo.route.repository;

import com.earseo.route.entity.Route;
import com.earseo.route.entity.RouteStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByMemberIdAndStatus(Long memberId, RouteStatus status);

    @EntityGraph(attributePaths = "items")
    Optional<Route> findWithItemsByMemberIdAndStatus(Long memberId, RouteStatus status);

    Page<Route> findByMemberIdAndStatusOrderByCreatedAtDesc(
            Long memberId,
            RouteStatus status,
            Pageable pageable
    );

    @Query("""
            select distinct r
            from Route r
            left join fetch r.items ri
            where r.id = :routeId
              and r.memberId = :memberId
            """)
    Optional<Route> findCompletedRouteDetail(
            @Param("routeId") Long routeId,
            @Param("memberId") Long memberId
    );
}
