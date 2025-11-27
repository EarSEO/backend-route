package com.earseo.route.repository;

import com.earseo.route.entity.RouteItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteItemRepository extends JpaRepository<RouteItem, Long> {
}
