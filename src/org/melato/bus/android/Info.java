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
package org.melato.bus.android;

import java.io.File;

import org.melato.android.AndroidLogger;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.client.NearbyManager;
import org.melato.bus.model.RouteManager;
import org.melato.log.Log;

import android.content.Context;

public class Info {
  public static final float MARK_PROXIMITY = 200f;
  private static RouteManager routeManager;
  
  public static RouteManager routeManager(Context context) {
    if ( routeManager == null ) {
      synchronized(Info.class) {
        if ( routeManager == null ) {
          context = context.getApplicationContext();
          Log.setLogger(new AndroidLogger(context));
          routeManager = new RouteManager(new SqlRouteStorage(context));          
        }
      }
    }
    return routeManager;
  }
  
  
  public static NearbyManager nearbyManager(Context context) {
    File cacheDir = context.getCacheDir();
    return new NearbyManager(routeManager(context), cacheDir); 
  }

}
