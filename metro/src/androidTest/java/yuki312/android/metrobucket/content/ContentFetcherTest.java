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

package yuki312.android.metrobucket.content;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import yuki312.android.metrobucket.content.net.JsonFetcher;
import yuki312.android.metrobucket.content.webapi.MetroConsumerKey;

import static yuki312.android.metrobucket.util.LogUtils.LOGW;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ContentFetcherTest extends ApplicationTestCase<Application> {
    private static final String TAG = makeLogTag(ContentFetcherTest.class);

    private static final String TINY_STATION_ID = "odpt.Station:TokyoMetro.Tozai.Gyotoku";
    private static final String BLOB_STATION_ID = "odpt.Station:TokyoMetro.Marunouchi.Ikebukuro";

    private CountDownLatch latch;

    public ContentFetcherTest() {
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

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        EventBus.getDefault().unregister(this);
    }

    public void test_fetchTinyStation() {
        eventbusRegister(singleStationFetchEvent);
        ContentFetcher.get().fetchStationAt(TINY_STATION_ID);
        waitLatch();
        eventbusUnregister(singleStationFetchEvent);
    }

    public void test_fetchBlobStation() {
        eventbusRegister(singleStationFetchEvent);
        ContentFetcher.get().fetchStationAt(BLOB_STATION_ID);
        waitLatch();
        eventbusUnregister(singleStationFetchEvent);
    }

    public void test_fetchAllStation() {
        eventbusRegister(singleStationFetchEvent);
        latch = new CountDownLatch(90);
        ContentFetcher.get().fetchAllStation();
        waitLatch();
        eventbusUnregister(singleStationFetchEvent);
    }

    public void test_fetchTinyRailway() {
        eventbusRegister(singleRailwayFetchEvent);
        ContentFetcher.get().fetchAllStation();
        waitLatch();
        eventbusUnregister(singleRailwayFetchEvent);
    }

    public void test_fetchTinyTimetable() {
        eventbusRegister(singleTimetableFetchEvent);
        ContentFetcher.get().fetchTimetableByStation(TINY_STATION_ID);
        waitLatch();
        eventbusUnregister(singleTimetableFetchEvent);
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

    private void eventbusRegister(EventListener listener) {
        EventBus.getDefault().register(listener);
    }

    private void eventbusUnregister(EventListener listener) {
        EventBus.getDefault().unregister(listener);
    }

    private interface EventListener {
    }

    private EventListener singleStationFetchEvent = new EventListener() {
        public void onEvent(ContentFetcher.SingleStationFetchEvent event) {
            countDown();
        }
    };

    private EventListener singleRailwayFetchEvent = new EventListener() {
        public void onEvent(ContentFetcher.SingleRailwayFetchEvent event) {
            countDown();
        }
    };

    private EventListener singleTimetableFetchEvent = new EventListener() {
        public void onEvent(ContentFetcher.SingleTimetableFetchEvent event) {
            countDown();
        }
    };
}