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

package yuki312.android.metrobucket.content.webapi;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static yuki312.android.metrobucket.util.LogUtils.LOGE;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

public class RailwayApi extends MetroWebApi<RailwayApi.Response[]> {
    private static final String TAG = makeLogTag(RailwayApi.class);

    public void fetch(String sameAs) {
        if (Strings.isNullOrEmpty(sameAs)) {
            throw new IllegalArgumentException("URN can not be null.");
        }

        String webapiUri = new MetroWebApiUri.Datapoints().appendPath(sameAs).build().toString();
        this.call(webapiUri, Response[].class);
    }

    public static class Response {
        @SerializedName("@context")
        public String context;

        @SerializedName("@id")
        public String id;

        @SerializedName("@type")
        public String type;

        @SerializedName("ug:region")
        public String region;

        @SerializedName("owl:sameAs")
        public String sameAs;

        @SerializedName("odpt:operator")
        public String operator;

        @SerializedName("dc:title")
        public String title;

        @SerializedName("dc:date")
        public String date;

        @SerializedName("odpt:stationOrder")
        public StationOrder[] stationOrder;

        @SerializedName("odpt:travelTime")
        public TravelTime[] travelTime;

        @SerializedName("odpt:lineCode")
        public String lineCode;

//        @SerializedName("odpt:womenOnlyCar")
//        public WomenOnlyCar[][] womenOnlyCar;

        public static class StationOrder {
            @SerializedName("odpt:station")
            public String station;

            @SerializedName("odpt:index")
            public int index;

            @Override
            public String toString() {
                return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
            }
        }

        public static class TravelTime {
            @SerializedName("odpt:fromStation")
            public String fromStation;

            @SerializedName("odpt:toStation")
            public String toStation;

            @SerializedName("odpt:necessaryTime")
            public String necessaryTime;

            @SerializedName("odpt:trainType")
            public String trainType;

            @Override
            public String toString() {
                return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
            }
        }

//        public static class WomenOnlyCar {
//            @SerializedName("odpt:fromStation")
//            public String fromStation;
//
//            @SerializedName("odpt:toStation")
//            public String toStation;
//
//            @SerializedName("odpt:operationDay")
//            public String operationDay;
//
//            @SerializedName("odpt:availableTimeFrom")
//            public String availableTimeFrom;
//
//            @SerializedName("odpt:avalilableTimeUntil")
//            public String avalilableTimeUntil;
//
//            @SerializedName("odpt:carComposition")
//            public String carComposition;
//
//            @SerializedName("odpt:carNumber")
//            public String carNumber;
//
//            @Override
//            public String toString() {
//                return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
//            }
//        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
