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

/**
 * 駅オブジェクト.
 * <p/>
 * - 駅についての最新情報をWebAPI経由で取得する.
 * - 駅についての情報を永続化する(永続化先はContentProviderまかせ)
 */
@Table(name = "Station", id = BaseColumns._ID)
public class Station extends Model {
    public String context;

    public String atId;

    public String type;

    @Column(name = "SameAs")
    public String sameAs;

    @Column(name = "Title")
    public String title;

    public String date;

    @Column(name = "Lng")
    public double lng;

    @Column(name = "Lat")
    public double lat;

    public String region;

    public String operator;

    @Column(name = "railway")
    public String railway;

    public String[] connectingRailway;

    public String facility;

    public String[] passengerSurvey;

    public String stationCode;

    public String[] exit;

    public StationTimetable[] timetable;

    public void storeRecord() {
        try {
            ActiveAndroid.beginTransaction();
            new Delete().from(Station.class).where("SameAs=?", this.sameAs).execute();
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
                .append(title == null ? "" : title)
                .append("Lng")
                .append(lng)
                .append("Lat")
                .append(lat)
                .append("Railway")
                .append(railway);
        String result = sb.toString();
        return String.format(Locale.US, "%08x%08x",
                result.hashCode(), result.length());
    }
//    private GeoGson fetchGeo(String regison) {
//        JsonFetcher fetcher = new JsonFetcher();
//        String geoUri = regison;
//        String consumerKey = MetroConsumerKey.get();
//        geoUri = Uri.parse(geoUri).buildUpon().appendQueryParameter(RdfProperty.Acl.consumerKey.label(), consumerKey).toString();
//        return fetcher.fetch(geoUri, GeoGson.class);
//    }
}
