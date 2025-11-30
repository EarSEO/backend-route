package com.earseo.route.repository;

import com.earseo.route.entity.RouteItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteItemRepository extends JpaRepository<RouteItem, Long> {
    List<RouteItem> findByRouteIdOrderByIdAsc(Long routeId);
}
