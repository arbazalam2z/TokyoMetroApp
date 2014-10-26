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

public class GeoApi extends MetroWebApi<GeoApi.Response> {
    public void fetch(String uri) {
        if (Strings.isNullOrEmpty(uri)) {
            throw new IllegalArgumentException("URN can not be null.");
        }

        String webapiUri = new MetroWebApiUri.Endpoint(uri).build().toString();
        this.call(webapiUri, Response.class);
    }

    /**
     * JSON format.
     * {
     *   coordinates": [
     *     Lat,
     *     Lon
     *   ],
     *   "type": "Point"
     * }
     */
    public class Response {
        @SerializedName("coordinates")
        public double coordinates[];

        @SerializedName("type")
        public String type;

        public double getLng() {
            return this.coordinates[0];
        }

        public double getLat() {
            return this.coordinates[1];
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
