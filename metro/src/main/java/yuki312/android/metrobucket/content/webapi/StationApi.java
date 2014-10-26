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

public class StationApi extends MetroWebApi<StationApi.Response[]> {
    public void fetchById(String sameAs) {
        if (Strings.isNullOrEmpty(sameAs)) {
            throw new IllegalArgumentException("sameAs can not be null.");
        }

        String webapiUri = new MetroWebApiUri.Datapoints().appendPath(sameAs).build().toString();
        this.call(webapiUri, Response[].class);
    }

    public void fetchAll() {
        String webapiUri = new MetroWebApiUri.Datapoints().putParam(RdfProperty.Rdf.type, "odpt:Station").build().toString();
        this.call(webapiUri, Response[].class);
    }

    public static class Response {
        @SerializedName("@context")
        public String context;

        @SerializedName("@id")
        public String id;

        @SerializedName("@type")
        public String type;

        @SerializedName("owl:sameAs")
        public String sameAs;

        @SerializedName("dc:title")
        public String title;

        @SerializedName("dc:date")
        public String date;

        @SerializedName("geo:long")
        public double lng;

        @SerializedName("geo:lat")
        public double lat;

        @SerializedName("ug:region")
        public String region;

        @SerializedName("odpt:operator")
        public String operator;

        @SerializedName("odpt:railway")
        public String railway;

        @SerializedName("odpt:connectingRailway")
        public String[] connectingRailway;

        @SerializedName("odpt:facility")
        public String facility;

        @SerializedName("odpt:passengerSurvey")
        public String[] passengerSurvey;

        @SerializedName("odpt:stationCode")
        public String stationCode;

        @SerializedName("odpt:exit")
        public String[] exit;

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
