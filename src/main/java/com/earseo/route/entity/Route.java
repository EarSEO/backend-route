package com.earseo.route.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RouteStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "path", nullable = false, columnDefinition = "geometry(LineString, 4326)")
    private LineString path;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteItem> items = new ArrayList<>();

    public static Route createInProgress(Long memberId, String name, LineString path) {
        Route route = new Route();
        route.memberId = memberId;
        route.name = name;
        route.status = RouteStatus.IN_PROGRESS;
        route.startedAt = LocalDateTime.now();
        route.createdAt = LocalDateTime.now();
        route.updatedAt = LocalDateTime.now();
        route.path = path;
        return route;
    }

    public void addItem(RouteItem item) {
        this.items.add(item);
        item.setRoute(this);
    }

    public void addItems(List<RouteItem> items) {
        for (RouteItem item : items) {
            addItem(item);
        }
    }

    public void complete() {
        this.status = RouteStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.startedAt == null) this.startedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void modifyName(String name){
        this.updatedAt = LocalDateTime.now();
        this.name = name;
    }
}
