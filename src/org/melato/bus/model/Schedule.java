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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/** A schedule maintains departure information for one route and for all days of the week. */
public class Schedule {
  private DaySchedule[] schedules;
  private String comment;
  /** The # of minutes after midnight where the schedule is still considered the previous day's schedule. */
  private int dayChange;

  static DecimalFormat d2Format = new DecimalFormat("00");
  
  /**
   * format a schedule time
   * @param time The time in minutes since midnight.
   * @return
   */
  public static String formatTime(int time) {
    return d2Format.format(time/60) + ":" + d2Format.format(time%60);
  }

  public static String formatDuration(int seconds) {
    if ( seconds < 3600 ) {
      return d2Format.format(seconds/60) + "'";
    } else {
      return d2Format.format(seconds/3600) + ":" + d2Format.format((seconds%3600)/60);
    }
  }
  
  /** Parse a hh:mm time and return minutes since midnight.*/
  public static int parseTime(String time) {
    int p = time.indexOf(':');
    if ( p < 0 )
      throw new IllegalArgumentException( "Invalid time: " + time );
    return Integer.parseInt(time.substring(0,p)) * 60 + Integer.parseInt(time.substring(p+1));
  }
    
  public Schedule(DaySchedule[] schedules) {
    super();
    this.schedules = schedules;
  }

  public DaySchedule[] getSchedules() {
    return schedules;
  }
  
  public DaySchedule getSchedule(ScheduleId id) {
    if (id == null)
      return null;
    for( DaySchedule d: getSchedules() ) {
      if ( id.equals(d.getScheduleId())) {
        return d;
      }
    }
    return null;    
  }
  
  public DaySchedule getSchedule(Date date) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    cal.add(Calendar.MINUTE, -dayChange); // shift the day back.
    int dateId = DateId.dateId(cal);
    for( DaySchedule daySchedule: schedules) {
      if (daySchedule.matchesDateId(dateId)) {
        return daySchedule;
      }
    }
    return DaySchedule.findSchedule(schedules, cal.get(Calendar.DAY_OF_WEEK));
  }
  
  /** Get the schedule times for a given day of the week. */
  public int[] getTimes( Date date ) {
    DaySchedule schedule = getSchedule(date);
    if ( schedule == null ) {
      return new int[0];
    }
    return schedule.getTimes();
  }
  
  /** Get the schedule times for a given day of the week. */
  public int[] getTimesForDayOfWeek( int dayOfWeek ) {
    DaySchedule schedule = DaySchedule.findSchedule(schedules, dayOfWeek);
    if ( schedule == null ) {
      return new int[0];
    }
    return schedule.getTimes();
  }
  
  /** Get the time in minutes since midnight */
  public static int getTime( Date date ) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int minute = cal.get(Calendar.MINUTE);
    return hour * 60 + minute;
  }

  /** For debugging. */
  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("Schedule:");
    for( DaySchedule ds: schedules ) {
      buf.append( " " + ds.getScheduleId() + "=" + ds.getTimes().length);
    }
    return buf.toString();
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public int getDayChange() {
    return dayChange;
  }

  public void setDayChange(int dayChange) {
    this.dayChange = dayChange;
  }  
}
