// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    boolean[] unavailableMinutes = new boolean[24 * 60];
    Collection<String> meetingAttendees = request.getAttendees();
    long meetingDuration = request.getDuration();

    for (Event event : events) {
      Set<String> eventAttendees = event.getAttendees();
      for (String meetingAttendee : meetingAttendees) {
        if (eventAttendees.contains(meetingAttendee)) {
          TimeRange eventTimeRange = event.getWhen();
          addEventTimeRange(unavailableMinutes, eventTimeRange);
          break;
        }
      }
    }
    
    Collection<TimeRange> availableTimeSlots =
        checkForAvailableTimeSlots(unavailableMinutes, meetingDuration);

    /*System.out.println("---> Unavailable Minutes <---");
    for (int i = 0; i < 24; i++) {
      System.out.print(i + ": ");
      for (int j = 0; j < 60; j++) {
        if (unavailableMinutes[(i * 60) + j]) System.out.print("1");
        else System.out.print("0");
      }
      System.out.println();
    }
    System.out.println("---> Time Ranges <---");
    for (TimeRange timeRange : availableTimeSlots) {
      System.out.println("Start: " + timeRange.start() + " - End: " + timeRange.end());
    }
    System.out.println();*/

    return availableTimeSlots;
  }

  /**
   * Adds the time range of a specific event to the array of unavailable minutes.
   * @param unavailableMinutes the minutes where other events are happening.
   * @param eventTimeRange the time range of a specific event to be added to unavailableMinutes.
   */
  private void addEventTimeRange(boolean[] unavailableTimeSlots, TimeRange eventTimeRange) {
    int start = eventTimeRange.start();
    int end = eventTimeRange.end() - 1;
    for (int i = start; i <= end; i++) {
      unavailableTimeSlots[i] = true;
    }
  }

  /**
   * Create a collection of available time slots with the information of unavailable minutes in the
   * day and the minimum duration required.
   * @param unavailableMinutes the minutes during the day where there can be no meetings.
   * @param duration the minimum duration required for the requested meeting.
   * @return a collection of time ranges where setting a meeting is possible.
   */
  private Collection<TimeRange> checkForAvailableTimeSlots(
      boolean[] unavailableMinutes, long duration) {
    if (duration > 24 * 60) {
      return Collections.emptyList();
    }

    List<TimeRange> availableTimeSlots = new ArrayList<>();
    boolean inTimeRange = false;
    int timeRangeStart = 0;
    int timeRangeEnd = 0;
    for (int i = 0; i < unavailableMinutes.length; i++) {
      // If the current minute is unavailable, check if it is inside a time range. If it is, change
      // inTimeRange to false add a new time range with the current start and end times to the
      // collection of available time slots if its duration is greater than the required.
      if (unavailableMinutes[i]) {
        if (inTimeRange) {
          inTimeRange = false;
          if(timeRangeEnd - timeRangeStart + 1 >= duration) {
            availableTimeSlots.add(TimeRange.fromStartEnd(timeRangeStart, timeRangeEnd, true));
          }
        }
      // If the current minute is available, check if it is inside a time range. If it is not,
      // change inTimeRange to true and mark the beginning of that new time range. If it is, update
      // the end of the current time range.
      } else {
        if (!inTimeRange) {
          inTimeRange = true;
          timeRangeStart = i;
        } else {
          timeRangeEnd = i;
        }
      }
    }

    // Check for last time range.
    if (inTimeRange) {
      availableTimeSlots.add(TimeRange.fromStartEnd(timeRangeStart, timeRangeEnd, true));
    }

    return availableTimeSlots;
  }
}
