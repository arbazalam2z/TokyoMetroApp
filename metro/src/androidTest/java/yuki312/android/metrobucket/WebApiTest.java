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

package yuki312.android.metrobucket;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import yuki312.android.metrobucket.content.model.Railway;
import yuki312.android.metrobucket.content.net.JsonFetcher;
import yuki312.android.metrobucket.content.webapi.MetroConsumerKey;
import yuki312.android.metrobucket.content.webapi.MetroWebApi;
import yuki312.android.metrobucket.content.webapi.RailwayApi;
import yuki312.android.metrobucket.content.webapi.StationApi;
import yuki312.android.metrobucket.content.webapi.StationTimetableApi;

import static yuki312.android.metrobucket.util.LogUtils.LOGW;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class WebApiTest extends ApplicationTestCase<Application> {
    private static final String TAG = makeLogTag(WebApiTest.class);

    private static final String TINY_STATION_ID = "odpt.Station:TokyoMetro.Tozai.Gyotoku";
    private static final String BLOB_STATION_ID = "odpt.Station:TokyoMetro.Marunouchi.Ikebukuro";

    private static final String TINY_RAILWAY_ID = "odpt.Railway:TokyoMetro.MarunouchiBranch";
    private static final String BLOB_RAILWAY_ID = "odpt.Railway:ATokyoMetro.Tozai";

    private CountDownLatch latch;

    public WebApiTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        JsonFetcher.initialize(getContext());
        MetroConsumerKey.initialize(getContext());
        latch = new CountDownLatch(1);
        LOGW(TAG, "==============" + this.getName());
    }

    public void test_fetchTinyStation() {
        String stationId = TINY_STATION_ID;
        StationApi api = new StationApi();
        api.listen(new MetroWebApi.WebApiListener<StationApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<StationApi.Response[]> response) {
                LOGW(TAG, "onResponse called.");
                LOGW(TAG, response.toString());
                countDown();
            }
        });
        api.fetchById(stationId);
        waitLatch();
    }

    public void test_fetchBlobStaionInfo() {
        String stationId = BLOB_STATION_ID;
        StationApi api = new StationApi();
        api.listen(new MetroWebApi.WebApiListener<StationApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<StationApi.Response[]> response) {
                LOGW(TAG, "onResponse called.");
                LOGW(TAG, response.toString());
                countDown();
            }
        });
        api.fetchById(stationId);
        waitLatch();
    }

    public void test_fetchAllStation() {
        StationApi api = new StationApi();
        api.listen(new MetroWebApi.WebApiListener<StationApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<StationApi.Response[]> response) {
                LOGW(TAG, "onResponse.");
                LOGW(TAG, response.toString());
                countDown();
            }
        });
        api.fetchAll();
        waitLatch();
    }

    public void test_fetchTinyRailway() {
        RailwayApi api = new RailwayApi();
        api.listen(new MetroWebApi.WebApiListener<RailwayApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<RailwayApi.Response[]> response) {
                LOGW(TAG, "onResponse.");
                LOGW(TAG, response.toString());
                countDown();
            }
        });
        api.fetch(TINY_RAILWAY_ID);
        waitLatch();
    }

    public void test_fetchBlobRailway() {
        RailwayApi api = new RailwayApi();
        api.listen(new MetroWebApi.WebApiListener<RailwayApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<RailwayApi.Response[]> response) {
                LOGW(TAG, "onResponse.");
                LOGW(TAG, response.toString());
                countDown();
            }
        });
        api.fetch(BLOB_RAILWAY_ID);
        waitLatch();
    }

    public void test_fetchStationTimetable() {
        StationTimetableApi api = new StationTimetableApi();
        api.listen(new MetroWebApi.WebApiListener<StationTimetableApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<StationTimetableApi.Response[]> response) {
                LOGW(TAG, "onResponse.");
                LOGW(TAG, response.toString());
                countDown();
            }
        });
        api.fetchByStation(TINY_STATION_ID);
        waitLatch();
    }

    public void test_fetchHanzomonRailway() {
        RailwayApi api = new RailwayApi();
        api.listen(new MetroWebApi.WebApiListener<RailwayApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<RailwayApi.Response[]> response) {
                LOGW(TAG, "onResponse.");
                LOGW(TAG, response.toString());
                countDown();
            }
        });
        api.fetch("odpt.Railway:TokyoMetro.Hanzomon");
        waitLatch();
    }

    private void countDown() {
        this.latch.countDown();
    }

    private void waitLatch() {
        try {
            this.latch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        EventBus.getDefault().unregister(this);
    }
}