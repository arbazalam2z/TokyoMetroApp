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

import yuki312.android.metrobucket.content.model.Railway;
import yuki312.android.metrobucket.content.webapi.RailwayApi;

public class RailwayMapper {
    public static Railway from(RailwayApi.Response response) {
        Railway railway = new Railway();
        railway.context = response.context;
        railway.atId = response.id;
        railway.type = response.type;
        railway.region = response.region;
        railway.sameAs = response.sameAs;
        railway.operator = response.operator;
        railway.title = response.title;
        railway.date = response.date;

        if (response.stationOrder != null) {
            final Railway.StationOrder[] stationOrders = new Railway.StationOrder[response.stationOrder.length];
            for (int i = 0; i < response.stationOrder.length; i++) {
                stationOrders[i] = RailwayMapper.from(response.stationOrder[i]);
            }
            railway.stationOrder = stationOrders;
        }

        if (response.travelTime != null) {
            final Railway.TravelTime[] travelTimes = new Railway.TravelTime[response.travelTime.length];
            for (int i = 0; i < response.travelTime.length; i++) {
                travelTimes[i] = RailwayMapper.from(response.travelTime[i]);
            }
            railway.travelTime = travelTimes;
        }

        railway.lineCode = response.lineCode;

//        // WomenOnlyCars属性は必須属性ではないためNullチェックが必須
//        final Railway.WomenOnlyCar[] womenOnlyCars;
//        if (response.womenOnlyCar != null) {
//            womenOnlyCars = new Railway.WomenOnlyCar[response.womenOnlyCar.length];
//            for (int i = 0; i < response.womenOnlyCar.length ; i++) {
//                womenOnlyCars[i] = RailwayMapper.from(response.womenOnlyCar[i][0]);  // TODO: 女性専用車両の２次元配列
//            }
//        } else {
//            womenOnlyCars = new Railway.WomenOnlyCar[0];  // Null object.
//        }
//        railway.womenOnlyCar = womenOnlyCars;

        return railway;
    }

    public static Railway.StationOrder from(RailwayApi.Response.StationOrder response) {
        Railway.StationOrder stationOrder = new Railway.StationOrder();
        stationOrder.station = response.station;
        stationOrder.index = response.index;
        return stationOrder;
    }

    public static Railway.TravelTime from(RailwayApi.Response.TravelTime response) {
        Railway.TravelTime travelTime = new Railway.TravelTime();
        travelTime.fromStation = response.fromStation;
        travelTime.toStation = response.toStation;
        travelTime.necessaryTime = response.necessaryTime;
        travelTime.trainType = response.trainType;
        return travelTime;
    }

//    public static Railway.WomenOnlyCar from(RailwayApi.Response.WomenOnlyCar response) {
//        Railway.WomenOnlyCar womenOnlyCar = new Railway.WomenOnlyCar();
//        womenOnlyCar.fromStation = response.fromStation;
//        womenOnlyCar.toStation = response.toStation;
//        womenOnlyCar.operationDay = response.operationDay;
//        womenOnlyCar.availableTimeFrom = response.availableTimeFrom;
//        womenOnlyCar.avalilableTimeUntil = response.avalilableTimeUntil;
//        womenOnlyCar.carComposition = response.carComposition;
//        womenOnlyCar.carNumber = response.carNumber;
//        return womenOnlyCar;
//    }

}
