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

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import org.apache.commons.lang3.ArrayUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import yuki312.android.metrobucket.content.model.Station;
import yuki312.android.metrobucket.content.model.StationTimetable;
import yuki312.android.metrobucket.content.net.JsonFetcher;
import yuki312.android.metrobucket.content.webapi.MetroConsumerKey;

import static yuki312.android.metrobucket.util.LogUtils.LOGW;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

public class MetroRepositoryTest extends ApplicationTestCase<Application> {
    private static final String TAG = makeLogTag(MetroRepositoryTest.class);

    private static final String TINY_STATION_ID = "odpt.Station:TokyoMetro.Tozai.Gyotoku";
    private static final String BLOB_STATION_ID = "odpt.Station:TokyoMetro.Marunouchi.Ikebukuro";

    private CountDownLatch latch;

    public MetroRepositoryTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        JsonFetcher.initialize(getContext());
        MetroConsumerKey.initialize(getContext());
        Configuration.Builder builder = new Configuration.Builder(getContext());
        builder.setDatabaseName("test.db");
        builder.setDatabaseVersion(1);
        builder.setModelClasses(Station.class);
        latch = new CountDownLatch(1);
        ActiveAndroid.initialize(builder.create(), true);
        LOGW(TAG, "==============" + this.getName());
    }

    public void test_stationIdCount() {
        String[] ids = ContentRepository.get().stationSameAsSets();
        assertTrue("Junk StationID.", ArrayUtils.isEmpty(ids));

        Station station = new Station();
        station.sameAs = "electric.sheep";
        ContentRepository.get().updateStationIfDiff(station);
        ids = ContentRepository.get().stationSameAsSets();
        assertTrue("Unexpected StationID count.", ids.length == 1);
    }

    public void test_timeTableFetch() {
        eventbusRegister(singleTimetableUpdateEvent);
        ContentFetcher.get().fetchTimetableByStation(TINY_STATION_ID);
        waitLatch();

        StationTimetable[] table = ContentRepository.get().findTimetableByStation(TINY_STATION_ID);
        assertTrue("Fetching count error.", table.length == 2);

        eventbusUnregister(singleTimetableUpdateEvent);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ActiveAndroid.dispose();
    }

    private void waitLatch() {
        try {
            this.latch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void countDown() {
        this.latch.countDown();
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

    private EventListener singleTimetableUpdateEvent = new EventListener() {
        public void onEvent(ContentRepository.UpdateTimetableEvent event) {
            countDown();
        }
    };
}
