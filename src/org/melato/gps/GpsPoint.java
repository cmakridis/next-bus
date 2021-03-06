/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.gps;


public class GpsPoint extends PointTime {
  private static final long serialVersionUID = 1L;
  /** GPX uses the term elevation, rather than altitude. */
  public float elevation = Float.NaN;
  public float speed = Float.NaN;
  public float bearing = Float.NaN;
  
  public float getElevation() {
    return elevation;
  }
  public void setElevation(float elevation) {
    this.elevation = elevation;
  }
  
  public void setAltitude(float elevation) {
    setElevation(elevation);
  }
  
  public float getAltitude() {
    return getElevation();
  }
  
  public float getSpeed() {
    return speed;
  }
  public void setSpeed(float speed) {
    this.speed = speed;
  }
    
  public float getBearing() {
    return bearing;
  }
  public void setBearing(float bearing) {
    this.bearing = bearing;
  }

  public GpsPoint() {    
  }
  public GpsPoint(float lat, float lon) {
    super(lat, lon);
  }
  public GpsPoint(Point2D p) {
    super(p);
  }
  public GpsPoint(PointTime p) {
    super(p);
  }
  
  public GpsPoint(GpsPoint p) {
    super(p);
    elevation = p.elevation;
    speed = p.speed;
    bearing = p.bearing;
  }

}
