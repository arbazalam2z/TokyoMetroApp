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

import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

public class StationTimetableApi extends MetroWebApi<StationTimetableApi.Response[]> {
    private static final String TAG = makeLogTag(StationTimetableApi.class);

    public void fetchByStation(String stationSameAs) {
        if (Strings.isNullOrEmpty(stationSameAs)) {
            throw new IllegalArgumentException("sameAs can not be null.");
        }

        String webapiUri = new MetroWebApiUri.Datapoints().putParam(RdfProperty.Rdf.type, "odpt:StationTimetable")
                .putParam(RdfProperty.Odpt.station, stationSameAs).build().toString();
        this.call(webapiUri, Response[].class);
    }

    public static class Response {
        @SerializedName("@context")
        public String context;

        @SerializedName("@id")
        public String id;

        @SerializedName("@type")
        public String type;

        @SerializedName("dc:date")
        public String date;

        @SerializedName("owl:sameAs")
        public String sameAs;

        @SerializedName("odpt:station")
        public String station;

        @SerializedName("odpt:railway")
        public String railway;

        @SerializedName("odpt:operator")
        public String operator;

        @SerializedName("odpt:railDirection")
        public String railDerection;

        @SerializedName("odpt:weekdays")
        public StationTimetableObject[] weekdays;

        @SerializedName("odpt:saturdays")
        public StationTimetableObject[] saturdays;

        @SerializedName("odpt:holidays")
        public StationTimetableObject[] holidays;


        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }

        public static class StationTimetableObject {
            @SerializedName("odpt:departureTime")
            public String departureTime;

            @SerializedName("odpt:destinationStation")
            public String destinationStation;

            @SerializedName("odpt:trainType")
            public String trainType;

            @SerializedName("odpt:isLast")
            public boolean isLast;

            @SerializedName("odpt:isOrigin")
            public boolean isOrigin;

            @SerializedName("odpt:carComposition")
            public int carComposition;

            @SerializedName("odpt:note")
            public String note;

            @Override
            public String toString() {
                return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
            }
        }
    }
}
