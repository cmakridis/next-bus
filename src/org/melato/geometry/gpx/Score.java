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
package org.melato.geometry.gpx;



public class Score implements Comparable<Score> {
  private Object  id;
  private int     count;
  
  
  /**
   * The id of the score, as passed in the constructor.
   * Used for identification.
   * @return
   */
  public Object getId() {
    return id;
  }
  
  public void setId(Object id) {
    this.id = id;
  }
  
  public Score() {
  }

  public Score(Object id) {
    super();
    this.id = id;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  @Override
  public int compareTo(Score score) {
    return score.count - count;
  }
  @Override
  public String toString() {
    return getId() + " " + count;
  }  
}
