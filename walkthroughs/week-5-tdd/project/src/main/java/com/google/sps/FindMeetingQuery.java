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

/**
 * Processes a query to find time ranges for a new meeting given the attendees required and other
 * events of the day.
 */
public final class FindMeetingQuery {
  /**
   * Finds all the available time slots for a meeting given the time required for the meeting, the
   * people that need to attend that meeting, and other events scheduled for the day. This
   * algorithm requires an additional space complexity of O(1), accounting for a boolean array of
   * fixed size (24 * 60) and a strictly smaller array of time ranges. It is O(mn) in time
   * complexity, 'm' being the number of attendees required for the meeting and 'n' being the
   * events of the day (the eventAttendees variable is a HashSet, making its lookup O(1), not
   * adding time complexity to the algorithm).
   * @param events all the events scheduled for a given day, with their attendees and time range.
   * @param request the request for a meeting, with their required attendees and minimum duration.
   * @return the available time slots during the day where setting the meeting is possible.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    boolean[] unavailableMinutes = new boolean[24 * 60];
    Collection<String> meetingAttendees = request.getAttendees();
    long meetingDuration = request.getDuration();

    for (Event event : events) {
      Set<String> eventAttendees = event.getAttendees();
      for (String meetingAttendee : meetingAttendees) {
        if (eventAttendees.contains(meetingAttendee)) {
          TimeRange eventTimeRange = event.getWhen();
          addUnavailableMinutes(unavailableMinutes, eventTimeRange);
          break;
        }
      }
    }
    
    Collection<TimeRange> availableTimeSlots =
        checkForAvailableTimeSlots(unavailableMinutes, meetingDuration);

    return availableTimeSlots;
  }

  /**
   * Adds the time range of a specific event to the array of unavailable minutes.
   * @param unavailableMinutes the minutes where other events are happening.
   * @param eventTimeRange the time range of a specific event to be added to unavailableMinutes.
   */
  private void addUnavailableMinutes(boolean[] unavailableMinutes, TimeRange eventTimeRange) {
    if (eventTimeRange.start() < 0 || eventTimeRange.end() - 1 > 24 * 60 - 1) return;
    for (int i = eventTimeRange.start(); i <= eventTimeRange.end() - 1; i++) {
      unavailableMinutes[i] = true;
    }
  }

  /**
   * Creates a collection of available time slots with the information of unavailable minutes in the
   * day and the minimum duration required.
   * @param unavailableMinutes the minutes during the day where there can be no meetings.
   * @param duration the minimum duration required for the requested meeting.
   * @return a collection of time ranges where setting a meeting is possible.
   */
  private Collection<TimeRange> checkForAvailableTimeSlots(
      boolean[] unavailableMinutes, long duration) {
    // If duration required for the meeting is longer than a day, meeting is not possible.
    if (duration > 24 * 60) {
      return Collections.emptyList();
    }

    List<TimeRange> availableTimeSlots = new ArrayList<>();
    boolean inAvailableTimeSlot = false;
    int availableTimeSlotStart = 0;
    int availableTimeSlotEnd = 0;
    for (int i = 0; i < unavailableMinutes.length; i++) {
      if (!unavailableMinutes[i] && !inAvailableTimeSlot) {
        // If the current minute is available but not in an available time slot, start the
        // available time slot and set this minute to the start of that time slot.
        inAvailableTimeSlot = true;
        availableTimeSlotStart = i;
      } else if (!unavailableMinutes[i] && inAvailableTimeSlot) {
        // If the current minute is available and already in an available time slot, update the end
        // of this time slot to this minute.
        availableTimeSlotEnd = i;
      } else if (inAvailableTimeSlot) {
        // If the current minute is unavailable but inside an available time slot, end the time
        // slot and add it to the list of available time slots if its duration is greater than the
        // required.
        inAvailableTimeSlot = false;
        if (availableTimeSlotEnd - availableTimeSlotStart + 1 >= duration) {
          availableTimeSlots.add(TimeRange.fromStartEnd(availableTimeSlotStart, availableTimeSlotEnd, true));
        }
      }
    }

    // Check for last available time slot.
    if (inAvailableTimeSlot) {
      availableTimeSlots.add(TimeRange.fromStartEnd(availableTimeSlotStart, availableTimeSlotEnd, true));
    }

    return availableTimeSlots;
  }
}
