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
package org.melato.android.gpx.map;

import org.melato.gpx.Waypoint;

import com.google.android.maps.OverlayItem;

public class WaypointOverlayItem extends OverlayItem {
	private Waypoint waypoint;
	
	public WaypointOverlayItem(Waypoint waypoint) {
		super( GMap.geoPoint(waypoint), null, waypoint.name);
		this.waypoint = waypoint;
	}

	public Waypoint getWaypoint() {
		return waypoint;
	}
	

}
