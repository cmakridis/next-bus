/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.gps.Earth;
import org.melato.gps.GlobalDistance;
import org.melato.gps.LocalDistance;
import org.melato.gps.Metric;
import org.melato.gps.Point2D;
import org.melato.progress.ProgressGenerator;
import org.melato.util.AbstractCollector;

/**
 * Provides read-access to routes, backed by a pluggable route database (RouteStorage).
 * For use on Java SE or Android
 * @author Alex Athanasopoulos
 *
 */
public class RouteManager {
  private RouteStorage storage;
  
  private List<Route> allRoutes;
  private List<Route> primaryRoutes;
  private Map<RouteId,Route> routeIndex;
  private RouteId cachedRouteId;
  private Route   cachedRoute;
  private Stop[]  cachedStops;
  private Schedule cachedSchedule;
    
  public RouteManager(RouteStorage storage) {
    super();
    this.storage = storage;
  }
  
  private List<Route> compact(List<Route> list) {
    return Arrays.asList(list.toArray(new Route[list.size()]));
  }
  
  public List<Route> getRoutes() {
    if ( allRoutes == null ) {
      synchronized( this ) {
        if ( allRoutes == null ) {
          allRoutes = compact(storage.loadRoutes());
          routeIndex = new HashMap<RouteId,Route>();
          for(Route route: allRoutes) {
            routeIndex.put(route.getRouteId(), route);            
          }
        }
      }
    }
    return allRoutes;
  }
  public Map<RouteId,Route> getRouteIndex() {
    getRoutes();
    return routeIndex;
  }
  
  public List<Route> getPrimaryRoutes() {
    if ( primaryRoutes == null ) {
      synchronized(this) {
        if ( primaryRoutes == null) {
          primaryRoutes = compact(storage.loadPrimaryRoutes());
        }
      }
    }
    return primaryRoutes;
  }
  
  public Route getRoute(RouteId routeId) {
    synchronized(this) {
      if ( isCached(routeId) && cachedRoute != null) {
        return cachedRoute;
      }
    }
    Route route = null;
    if (allRoutes != null) {
      route = routeIndex.get(routeId);
    } else {
      route = storage.loadRoute(routeId);
    }
    synchronized(this) {
      setCachedRouteId(routeId);
      cachedRoute = route;
    }
    return route;
  }
  
  public Schedule getSchedule(RouteId routeId) {
    synchronized(this) {
      if ( isCached(routeId) && cachedSchedule != null) {
        return cachedSchedule;
      }
    }
    Schedule schedule = storage.loadSchedule(routeId);
    synchronized(this) {
      setCachedRouteId(routeId);
      cachedSchedule = schedule;
    }
    return schedule;
  }

  public Schedule getSchedule(Route route) {
    return getSchedule(route.getRouteId());
  }

  private boolean isCached(RouteId routeId) {
    return cachedRouteId != null && cachedRouteId.equals(routeId);
  }
  private synchronized void setCachedRouteId(RouteId routeId) {
    if ( ! isCached(routeId)) {
      cachedRouteId = routeId;
      cachedRoute = null;
      cachedStops = null;
      cachedSchedule = null;
    }
  }

  /**
   * Get the list or stops for the route.
   * Each stop defines
   * It defines:
   *  - lat, lon - The stops coordinates
   *  - sym - The stop symbol
   *  - name - The stop label
   *  - time - The duration from the previous stop (in milliseconds)
   *  - deviation - The statistical deviation from the stated duration
   * */
  public Stop[] getStops(RouteId routeId) {
    synchronized(this) {
      if ( isCached(routeId) && cachedStops != null) {
        return cachedStops;
      }
    }
    Stop[] stops = storage.loadStops(routeId).toArray(new Stop[0]);
    synchronized(this) {
      setCachedRouteId(routeId);
      cachedStops = stops;
    }
    return stops;
  }

  public Stop[] getStops(Route route) {
    return getStops(route.getRouteId());
  }


  public String getUri( Route route ) {
    return storage.getUri(route.getRouteId());
  }
  /**
   * Load marker information, which includes all routes that go through the given stop.
   * @param symbol
   * @return
   */
  public synchronized MarkerInfo loadMarker(String symbol) {
    return storage.loadMarker(symbol);
  }

  static class DistanceFilter extends AbstractCollector<Marker> {
    Collection<Marker> result;
    
    private Point2D center;
    private float distance;
    
    public DistanceFilter(List<Marker> result, Point2D center, float distance) {
      super();
      this.result = result;
      this.center = center;
      this.distance = distance;
    }

    @Override
    public boolean add(Marker p) {
      if ( Earth.distance(center, p) < distance ) {
        result.add(p);
        size++;
        return true;
      }
      return false;
    }    
  }
  
  public void iterateNearbyRoutes(Point2D point, float latitudeDifference,
      float longitudeDifference, Collection<RouteId> collector) {
    storage.iterateNearbyRoutes(point, latitudeDifference, longitudeDifference,
        collector);
  }

  public List<Marker> findNearbyStops(Point2D point, float distance) {
    List<Marker> result = new ArrayList<Marker>();
    DistanceFilter filter = new DistanceFilter(result, point, distance);
    float latDiff = Earth.latitudeForDistance(distance);
    float lonDiff = Earth.longitudeForDistance(distance, point.getLat());
    storage.iterateNearbyStops(point, latDiff, lonDiff, filter);
    return result;
  }

  

  public void iterateAllRouteStops(RouteStopCallback callback) {
    ProgressGenerator progress = ProgressGenerator.get();
    progress.setLimit( getRoutes().size() );
    storage.iterateAllRouteStops(callback);
  }

  public void iteratePrimaryRouteStops(RouteStopCallback callback) {
    storage.iteratePrimaryRouteStops(callback);
  }

  /** Get a center point for the whole route collection. */
  public Point2D getCenter() {
    Point2D center = storage.getCenter();
    if ( center == null ) {
      center = new Point2D( 37.975086f, 23.735683f); // hardcoded Syntagma Square.
    }
    return center;    
  }

  public Metric getMetric() {
    Point2D center = getCenter();
    if ( center != null ) {
      return new LocalDistance(center);
    }
    return new GlobalDistance();
  }
  
  public void benchmark() {
    iterateAllRouteStops(new RouteStopCallback() {

      @Override
      public void add(RouteId routeId, List<Point2D> waypoints) {
      }
      
    });
  }   

  public RouteStorage getStorage() {
    return storage;
  }  
}
