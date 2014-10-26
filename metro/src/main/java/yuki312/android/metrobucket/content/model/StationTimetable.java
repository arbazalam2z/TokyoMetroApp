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

package yuki312.android.metrobucket.content.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StationTimetable {
    public String context;

    public String id;

    public String type;

    public String date;

    public String sameAs;

    public String station;

    public String railway;

    public String operator;

    public String railDerection;

    public StationTimetableObject[] weekdays;

    public StationTimetableObject[] saturdays;

    public StationTimetableObject[] holidays;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public static class StationTimetableObject {
        public String departureTime;

        public String destinationStation;

        public String trainType;

        public boolean isLast;

        public boolean isOrigin;

        public int carComposition;

        public String note;

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
