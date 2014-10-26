/*
 * Copyright 2014 yuki312 All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yuki312.android.metrobucket.content.mapper;


import yuki312.android.metrobucket.content.model.StationTimetable;
import yuki312.android.metrobucket.content.webapi.StationTimetableApi;

public class StationTimetableMapper {
    public static StationTimetable from(StationTimetableApi.Response response) {
        StationTimetable stationTimetable = new StationTimetable();
        stationTimetable.context = response.context;
        stationTimetable.id = response.id;
        stationTimetable.type = response.type;
        stationTimetable.date = response.date;
        stationTimetable.operator = response.operator;
        stationTimetable.railDerection = response.railDerection;
        stationTimetable.railway = response.railway;
        stationTimetable.sameAs = response.sameAs;
        stationTimetable.date = response.date;
        stationTimetable.station = response.station;

        final StationTimetable.StationTimetableObject[] holidays = new StationTimetable.StationTimetableObject[response.holidays.length];
        for (int i = 0; i < response.holidays.length; i++) {
            holidays[i] = StationTimetableMapper.from(response.holidays[i]);
        }
        stationTimetable.holidays = holidays;

        final StationTimetable.StationTimetableObject[] weekdays = new StationTimetable.StationTimetableObject[response.weekdays.length];
        for (int i = 0; i < response.weekdays.length; i++) {
            weekdays[i] = StationTimetableMapper.from(response.weekdays[i]);
        }
        stationTimetable.weekdays = weekdays;

        final StationTimetable.StationTimetableObject[] saturdays = new StationTimetable.StationTimetableObject[response.saturdays.length];
        for (int i = 0; i < response.saturdays.length; i++) {
            saturdays[i] = StationTimetableMapper.from(response.saturdays[i]);
        }
        stationTimetable.saturdays = saturdays;

        return stationTimetable;
    }

    public static StationTimetable.StationTimetableObject from(StationTimetableApi.Response.StationTimetableObject response) {
        StationTimetable.StationTimetableObject object = new StationTimetable.StationTimetableObject();
        object.carComposition = response.carComposition;
        object.departureTime = response.departureTime;
        object.destinationStation = response.destinationStation;
        object.isLast = response.isLast;
        object.isOrigin = response.isOrigin;
        object.note = response.note;
        return object;
    }
}
