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

import yuki312.android.metrobucket.content.model.Station;
import yuki312.android.metrobucket.content.webapi.StationApi;

public abstract class StationMapper {
    public static Station from(StationApi.Response response) {
        Station station = new Station();
        station.connectingRailway = response.connectingRailway;
        station.context = response.context;
        station.date = response.date;
        station.lng = response.lng;
        station.lat = response.lat;
        station.exit = response.exit;
        station.facility = response.facility;
        station.atId = response.id;
        station.operator = response.operator;
        station.passengerSurvey = response.passengerSurvey;
        station.railway = response.railway;
        station.region = response.region;
        station.sameAs = response.sameAs;
        station.stationCode = response.stationCode;
        station.title = response.title;
        station.type = response.type;
        return station;
    }
}
