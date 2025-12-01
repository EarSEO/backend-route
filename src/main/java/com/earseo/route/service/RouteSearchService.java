package com.earseo.route.service;

import com.earseo.route.controller.client.StoryFeignClient;
import com.earseo.route.dto.request.GetRouteListSpotRequest;
import com.earseo.route.dto.request.PathLineStringRequest;
import com.earseo.route.dto.request.PointRequest;
import com.earseo.route.dto.response.GetRouteListSpotResponse;
import com.earseo.route.dto.response.GetRouteSpotResponse;
import com.earseo.route.dto.response.SightMetaResponse;
import com.earseo.route.entity.RouteRefType;
import com.earseo.route.service.route.RoutePathPoint;
import com.earseo.route.service.route.RouteSearchItem;
import com.earseo.route.service.route.RouteSearchResult;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteSearchService {

    private final GraphHopperService graphHopperService;
    private final StoryFeignClient storyFeignClient;

    public RouteSearchResult findRoute(Long memberId, List<SightMetaResponse> sights) {

        GeometryFactory geometryFactory = new GeometryFactory();

        List<Coordinate> allCoords = new ArrayList<>();

        List<PathLineStringRequest> paths = new ArrayList<>();

        List<RoutePathPoint>  routes = new ArrayList<>();

        for(int i=0;i<sights.size()-1;i++){
            SightMetaResponse start = sights.get(i);
            SightMetaResponse end = sights.get(i+1);

            GHPoint startPt = new GHPoint(start.latitude(), start.longitude());
            GHPoint endPt   = new GHPoint(end.latitude(), end.longitude());

            GHRequest req = new GHRequest(startPt, endPt)
                    .setProfile("foot_custom")
                    .setLocale("ko");

            GHResponse res = graphHopperService.getHopper().route(req);

            if (res.hasErrors()) {
                throw new RuntimeException(res.getErrors().toString());
            }

            PointList points = res.getBest().getPoints();

            List<PointRequest> pointRequests = new ArrayList<>();

            for (int p = 0; p < points.size(); p++) {
                double lat = points.getLat(p);
                double lon = points.getLon(p);

                pointRequests.add(new PointRequest(lat, lon));
                routes.add(new RoutePathPoint(lat, lon));

                allCoords.add(new Coordinate(lon, lat));
            }

            paths.add(new PathLineStringRequest(pointRequests,1L));
        }

        GetRouteListSpotResponse routeListSpotResponse = storyFeignClient.getPathsSpotList(new GetRouteListSpotRequest(paths,null,50L));


        Coordinate[] coordArray = allCoords.toArray(new Coordinate[0]);
        LineString  lineString = geometryFactory.createLineString(coordArray);


        return new RouteSearchResult(routes, getItems(sights,routeListSpotResponse));
    }

    private List<RouteSearchItem> getItems(List<SightMetaResponse> sights, GetRouteListSpotResponse routeListSpotResponse) {
        List<RouteSearchItem> items = new ArrayList<>();
        List<GetRouteSpotResponse> getRouteSpotResponses = routeListSpotResponse.spotList().getFirst();

        for(int i=0;i<sights.size();i++){
            SightMetaResponse sight = sights.get(i);
            RouteSearchItem sightItem = new RouteSearchItem(RouteRefType.SIGHT,sight.id(), sight.name(),
                    sight.imageUrl(), sight.address(), sight.latitude(), sight.longitude(), sight.docentUrl(), sight.theme(),null);
            items.add(sightItem);

            if(i != sights.size()-1){
                GetRouteSpotResponse story =  getRouteSpotResponses.get(i);
                RouteSearchItem spotItem= new RouteSearchItem(RouteRefType.STORY_SPOT,String.valueOf(story.storySpotId()),story.title(),
                        null,null,story.latitude(),story.longitude(), story.docentUrl(), story.storyConcept().toString(),story.summaryId());
                items.add(spotItem);
            }
        }

        return  items;
    }
}
