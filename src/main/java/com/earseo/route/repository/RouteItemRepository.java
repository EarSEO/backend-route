package com.earseo.route.repository;

import com.earseo.route.entity.RouteItem;
import com.earseo.route.entity.RouteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteItemRepository extends JpaRepository<RouteItem, Long> {
    @Query("""
        select ri
        from RouteItem ri
        join fetch ri.route r
        where ri.id = :routeItemId
          and r.memberId = :memberId
          and r.status = :status
    """)
    Optional<RouteItem> findWithRouteByIdAndMemberIdAndRouteStatus(
            @Param("routeItemId") Long routeItemId,
            @Param("memberId") Long memberId,
            @Param("status") RouteStatus status
    );
}