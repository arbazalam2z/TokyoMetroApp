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

import android.provider.BaseColumns;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Locale;


@Table(name = "Railway", id = BaseColumns._ID)
public class Railway extends Model {
    public String context;

    public String atId;

    public String type;

    public String region;

    @Column(name = "SameAs")
    public String sameAs;

    public String operator;

    @Column(name = "Title")
    public String title;

    public String date;

    public StationOrder[] stationOrder;

    public TravelTime[] travelTime;

    public String lineCode;

    public WomenOnlyCar[] womenOnlyCar;

    public static class StationOrder {
        public String station;

        public int index;
    }

    public static class TravelTime {
        public String fromStation;

        public String toStation;

        public String necessaryTime;

        public String trainType;
    }

    public static class WomenOnlyCar {
        public String fromStation;

        public String toStation;

        public String operationDay;

        public String availableTimeFrom;

        public String avalilableTimeUntil;

        public String carComposition;

        public String carNumber;
    }


    public void storeRecord() {
        try {
            ActiveAndroid.beginTransaction();
            new Delete().from(Railway.class).where("SameAs=?", this.sameAs).execute();
            save();
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public String getUpdateHashcode() {
        StringBuilder sb = new StringBuilder();
        sb.append("SameAs").append(sameAs == null ? "" : sameAs)
                .append("Title")
                .append(title == null ? "" : title);
        String result = sb.toString();
        return String.format(Locale.US, "%08x%08x",
                result.hashCode(), result.length());
    }
}
