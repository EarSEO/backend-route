package com.earseo.route.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "route_item")
public class RouteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Enumerated(EnumType.STRING)
    @Column(name = "ref_type", nullable = false, length = 20)
    private RouteRefType refType;

    @Column(name = "ref_id", nullable = false)
    private String refId;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "docent_url", nullable = false)
    private String docentUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "theme", length = 50)
    private String theme;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static RouteItem of(
            RouteRefType refType,
            String refId,
            String name,
            String imageUrl,
            String address,
            Double latitude,
            Double longitude,
            String docentUrl,
            String theme
    ) {
        RouteItem item = new RouteItem();
        item.refType = refType;
        item.refId = refId;
        item.name = name;
        item.imageUrl = imageUrl;
        item.address = address;
        item.latitude = latitude;
        item.longitude = longitude;
        item.docentUrl = docentUrl;
        item.theme = theme;
        return item;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    void setRoute(Route route) {
        this.route = route;
    }
}
