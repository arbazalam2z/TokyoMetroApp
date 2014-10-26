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

import com.google.common.base.Strings;

import org.apache.commons.lang3.ArrayUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.greenrobot.event.EventBus;
import yuki312.android.metrobucket.content.mapper.RailwayMapper;
import yuki312.android.metrobucket.content.mapper.StationMapper;
import yuki312.android.metrobucket.content.mapper.StationTimetableMapper;
import yuki312.android.metrobucket.content.model.Railway;
import yuki312.android.metrobucket.content.model.Station;
import yuki312.android.metrobucket.content.model.StationTimetable;
import yuki312.android.metrobucket.content.webapi.MetroWebApi;
import yuki312.android.metrobucket.content.webapi.RailwayApi;
import yuki312.android.metrobucket.content.webapi.StationApi;
import yuki312.android.metrobucket.content.webapi.StationTimetableApi;

import static yuki312.android.metrobucket.util.LogUtils.DEBUG;
import static yuki312.android.metrobucket.util.LogUtils.LOGD;
import static yuki312.android.metrobucket.util.LogUtils.LOGE;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

public final class ContentFetcher {
    private static final String TAG = makeLogTag(ContentFetcher.class);
    private Executor fetchDeferExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    });
    private Executor fetchPriorityExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setPriority(Thread.MAX_PRIORITY);
            return t;
        }
    });

    public void fetchRailwayAt(String railwaySameAs) {
        if (ContentRepository.get().findRailway(railwaySameAs) != null) {
            // TODO: force fetching.
            return;
        }
        fetchRailwayAt(railwaySameAs, false);
    }

    public void fetchRailwayAt(String railwaySameAs, boolean priority) {
        if (ContentRepository.get().findRailway(railwaySameAs) != null) {
            // TODO: force fetching.
            return;
        }

        if (priority) {
            fetchPriorityExecutor.execute(new SingleRailwayBuilder(railwaySameAs));
        } else {
            fetchDeferExecutor.execute(new SingleRailwayBuilder(railwaySameAs));
        }
    }

    public void fetchStationAt(String stationSameAs) {
        if (ContentRepository.get().findStation(stationSameAs) != null) {
            // TODO: force fetching.
            return;
        }
        fetchStationAt(stationSameAs, false);
    }

    public void fetchStationAt(String stationSameAs, boolean priority) {
        if (ContentRepository.get().findStation(stationSameAs) != null) {
            // TODO: force fetching.
            return;
        }

        if (priority) {
            fetchPriorityExecutor.execute(new SingleStationBuilder(stationSameAs));
        } else {
            fetchDeferExecutor.execute(new SingleStationBuilder(stationSameAs));
        }
    }

    public void fetchAllStation() {
        fetchDeferExecutor.execute(new MultiStationBuilder());
    }

    public void fetchTimetableByStation(String stationSameAs) {
        fetchDeferExecutor.execute(new SingleTimetableBuilder(stationSameAs));
    }

    private static class SingleRailwayBuilder implements Runnable {
        private final String sameAs;
        private Railway result;
        private CountDownLatch latcher;

        private SingleRailwayBuilder(String sameAs) {
            if (Strings.isNullOrEmpty(sameAs)) {
                throw new IllegalArgumentException("sameAs can not be null.");
            }
            this.sameAs = sameAs;
        }

        @Override
        public synchronized void run() {
            try {
                latchOn();
                RailwayApi railwayApi = new RailwayApi();
                railwayApi.listen(railwayApiListener);
                railwayApi.fetch(sameAs);
                await();

                ContentRepository.get().updateRailwayIfDiff(result);
            } catch (Exception e) {
                LOGE(TAG, "single railway fetch error. " + e);
                if (DEBUG(TAG)) {
                    e.printStackTrace();
                }
                return;
            } finally {
                EventBus.getDefault().post(new SingleRailwayFetchEvent());
            }
        }

        private MetroWebApi.WebApiListener<RailwayApi.Response[]> railwayApiListener
                = new MetroWebApi.WebApiListener<RailwayApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<RailwayApi.Response[]> response) {
                LOGD(TAG, "onResponse. RailwayWebApi");
                if (DEBUG(TAG)) {
                    LOGD(TAG, response.toString());
                }

                if (ArrayUtils.isEmpty(response.obj)) {
                    SingleRailwayBuilder.this.result = null;
                } else if (response.obj.length != 1) {
                    throw new IllegalStateException("RailwayApiのレスポンスが期待する数(1)より多い.");
                } else {
                    SingleRailwayBuilder.this.result = RailwayMapper.from(response.obj[0]);
                }
                SingleRailwayBuilder.this.latchOff();
            }
        };

        private void latchOn() {
            latcher = new CountDownLatch(1);
        }

        private void latchOff() {
            latcher.countDown();
        }

        private void await() throws InterruptedException, TimeoutException {
            if (!latcher.await(10, TimeUnit.SECONDS)) {
                throw new TimeoutException("Time out");
            }
        }
    }

    private static class SingleStationBuilder implements Runnable {
        private final String sameAs;
        private Station result;
        private CountDownLatch latcher;

        private SingleStationBuilder(String sameAs) {
            if (Strings.isNullOrEmpty(sameAs)) {
                throw new IllegalArgumentException("sameAs can not be null.");
            }
            this.sameAs = sameAs;
        }

        @Override
        public synchronized void run() {
            try {
                latchOn();
                StationApi api = new StationApi();
                api.listen(stationApiListener);
                api.fetchById(this.sameAs);
                await();

                if (this.result != null) {
//                    GEOはレスポンスに付随するため再問い合わせ不要
//                    latchOn();
//                    GeoApi geoApi = new GeoApi();
//                    geoApi.listen(geoApiListener);
//                    geoApi.fetch(result.region);
//                    await();

                    ContentFetcher.get().fetchRailwayAt(result.railway);
                    ContentRepository.get().updateStationIfDiff(result);
                }

            } catch (Exception e) {
                LOGE(TAG, "single station fetch error. " + e);
                if (DEBUG(TAG)) {
                    e.printStackTrace();
                }
                return;
            } finally {
                EventBus.getDefault().post(new SingleStationFetchEvent());
            }
        }

        private MetroWebApi.WebApiListener<StationApi.Response[]> stationApiListener
                = new MetroWebApi.WebApiListener<StationApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<StationApi.Response[]> response) {
                LOGD(TAG, "onResponse. StationWebAPI");
                if (DEBUG(TAG)) {
                    LOGD(TAG, response.toString());
                }

                if (ArrayUtils.isEmpty(response.obj)) {
                    LOGD(TAG, "StationAPIのレスポンスが空.");
                } else if (response.obj.length != 1) {
                    throw new IllegalStateException("StationAPIのレスポンスが期待する数(1)より多い.");
                } else {
                    SingleStationBuilder.this.result = StationMapper.from(response.obj[0]);
                }

                SingleStationBuilder.this.latchOff();
            }
        };

//        private MetroWebApi.WebApiListener<GeoApi.Response> geoApiListener
//                = new MetroWebApi.WebApiListener<GeoApi.Response>() {
//            @Override
//            public void onResponse(MetroWebApi.ApiResponse<GeoApi.Response> response) {
//                LOGD(TAG, "onResponse GeoWebAPI.");
//                if (DEBUG(TAG)) {
//                    LOGD(TAG, response.toString());
//                }
//
//                if (response.obj == null) {
//                    throw new IllegalStateException("GeoAPIのレスポンスが空.");
//                }
//
//                SingleStationBuilder.this.result.geo = GeoMapper.from(response.obj);
//                SingleStationBuilder.this.latchOff();
//            }
//        };

        private MetroWebApi.WebApiListener<RailwayApi.Response[]> railwayApiListener
                = new MetroWebApi.WebApiListener<RailwayApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<RailwayApi.Response[]> response) {
                LOGD(TAG, "onResponse RailwayWebAPI.");
                if (DEBUG(TAG)) {
                    LOGD(TAG, response.toString());
                }

                if (ArrayUtils.isEmpty(response.obj)) {
                    throw new IllegalStateException("RailwayAPIのレスポンスが空.");
                } else if (response.obj.length != 1) {
                    throw new IllegalStateException("RailwayAPIのレスポンスが期待する数(1)より多い.");
                }

            }
        };

        private void latchOn() {
            latcher = new CountDownLatch(1);
        }

        private void latchOff() {
            latcher.countDown();
        }

        private void await() throws InterruptedException, TimeoutException {
            if (!latcher.await(10, TimeUnit.SECONDS)) {
                throw new TimeoutException("Time out");
            }
        }

    }

    private static class MultiStationBuilder implements Runnable {
        @Override
        public void run() {
            StationApi api = new StationApi();

            try {
                api.listen(stationApiListener);
                api.fetchAll();
            } catch (Exception e) {
                LOGE(TAG, "fetch error.");
                return;
            }
        }

        private MetroWebApi.WebApiListener<StationApi.Response[]> stationApiListener
                = new MetroWebApi.WebApiListener<StationApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<StationApi.Response[]> responses) {
                LOGD(TAG, "onResponse. StationWebAPI");
                if (DEBUG(TAG)) {
                    LOGD(TAG, responses.toString());
                }

                if (ArrayUtils.isEmpty(responses.obj)) {
                    throw new IllegalStateException("StationAPIのレスポンスが空.");
                }

                for (StationApi.Response station : responses.obj) {
                    ContentFetcher.get().fetchStationAt(station.sameAs);
                }
            }
        };
    }

    private static class SingleTimetableBuilder implements Runnable {
        private final String sameAs;
        private StationTimetable[] result;
        private CountDownLatch latcher;

        private SingleTimetableBuilder(String sameAs) {
            if (Strings.isNullOrEmpty(sameAs)) {
                throw new IllegalArgumentException("sameAs can not be null.");
            }
            this.sameAs = sameAs;
        }

        @Override
        public synchronized void run() {
            try {
                latchOn();
                StationTimetableApi api = new StationTimetableApi();
                api.listen(timetableApiListener);
                api.fetchByStation(sameAs);
                await();

                ContentRepository.get().updateTimetable(result);
            } catch (Exception e) {
                LOGE(TAG, "single timetable fetch error. " + e);
                if (DEBUG(TAG)) {
                    e.printStackTrace();
                }
                return;
            } finally {
                EventBus.getDefault().post(new SingleTimetableFetchEvent());
            }
        }

        private MetroWebApi.WebApiListener<StationTimetableApi.Response[]> timetableApiListener
                = new MetroWebApi.WebApiListener<StationTimetableApi.Response[]>() {
            @Override
            public void onResponse(MetroWebApi.ApiResponse<StationTimetableApi.Response[]> response) {
                LOGD(TAG, "onResponse. StationTimetableWebApi");
                if (DEBUG(TAG)) {
                    LOGD(TAG, response.toString());
                }

                if (ArrayUtils.isEmpty(response.obj)) {
                    SingleTimetableBuilder.this.result = null;
                } else {
                    SingleTimetableBuilder.this.result = new StationTimetable[response.obj.length];
                    for (int i = 0; i < response.obj.length; i++) {
                        SingleTimetableBuilder.this.result[i] = StationTimetableMapper.from(response.obj[i]);
                    }
                }
                SingleTimetableBuilder.this.latchOff();
            }
        };

        private void latchOn() {
            latcher = new CountDownLatch(1);
        }

        private void latchOff() {
            latcher.countDown();
        }

        private void await() throws InterruptedException, TimeoutException {
            if (!latcher.await(10, TimeUnit.SECONDS)) {
                throw new TimeoutException("Time out");
            }
        }
    }


    public static class SingleRailwayFetchEvent {
    }

    public static class SingleStationFetchEvent {
    }

    public static class MultiStationFetchEvent {
    }

    public static class SingleTimetableFetchEvent {
    }

    private static final ContentFetcher instance = new ContentFetcher();

    public static ContentFetcher get() {
        return instance;
    }

    private ContentFetcher() { /* Singleton */ }
}
