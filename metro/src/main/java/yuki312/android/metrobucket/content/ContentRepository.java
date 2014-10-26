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

import android.util.LruCache;

import com.activeandroid.query.Select;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.greenrobot.event.EventBus;
import yuki312.android.metrobucket.content.model.Railway;
import yuki312.android.metrobucket.content.model.Station;
import yuki312.android.metrobucket.content.model.StationTimetable;

import static yuki312.android.metrobucket.util.LogUtils.LOGD;
import static yuki312.android.metrobucket.util.LogUtils.LOGI;
import static yuki312.android.metrobucket.util.LogUtils.LOGW;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

public final class ContentRepository {
    private static final String TAG = makeLogTag(ContentRepository.class);

    private final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    private HashMap<String, Railway> railwayMap = new HashMap<String, Railway>();
    private HashMap<String, Station> stationMap = new HashMap<String, Station>();
    private LruCache<String, StationTimetable[]> timetableLruCache = new LruCache<String, StationTimetable[]>(5);

    public String[] stationSameAsSets() {
        return stationMap.keySet().toArray(new String[]{});
    }

    public Station findStation(String sameAs) {
        return stationMap.get(sameAs);
    }

    public Station findStationOrFetchIfNeeded(String sameAs, boolean hurryFetch) {
        Station station = stationMap.get(sameAs);
        if (station == null) {
            ContentFetcher.get().fetchStationAt(sameAs, hurryFetch);
        }
        return station;
    }

    public Railway findRailway(String sameAs) {
        return railwayMap.get(sameAs);
    }

    public Railway findRailwayOrFetchIfNeeded(String sameAs, boolean hurryFetch) {
        Railway railway = railwayMap.get(sameAs);
        if (railway == null) {
            ContentFetcher.get().fetchRailwayAt(sameAs, hurryFetch);
        }
        return railway;
    }

    public StationTimetable[] findTimetableByStation(String stationSameAs) {
        return timetableLruCache.get(stationSameAs);
    }

    public StationTimetable[] findTimetableByStation(String stationSameAs, boolean fetchIfNeeded) {
        StationTimetable[] timetables = timetableLruCache.get(stationSameAs);
        if (timetables == null && fetchIfNeeded) {
            ContentFetcher.get().fetchTimetableByStation(stationSameAs);
        }
        return timetables;
    }

    void updateRailwayIfDiff(Railway railway) {
        if (railway == null) {
            LOGW(TAG, "update railway canceled(empty items).");
            return;
        }

        try {
            LOCK.writeLock().lock();

            // difference check.
            boolean updated;
            if (railwayMap.containsKey(railway.sameAs)) {
                Railway stored = railwayMap.get(railway.sameAs);
                // WeakHashが異なるときはRailwayデータの更新を行う
                updated = !(railway.getUpdateHashcode().equals(stored.getUpdateHashcode()));
            } else {
                updated = true;
            }

            if (updated) {
                LOGI(TAG, "update railway. " + railway.sameAs);
                railwayMap.put(railway.sameAs, railway);
                railway.storeRecord();
                EventBus.getDefault().post(new UpdateRailwayEvent(railway.sameAs));
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    void updateStationIfDiff(Station station) {
        if (station == null) {
            LOGW(TAG, "update station canceled(empty items).");
            return;
        }

        try {
            LOCK.writeLock().lock();

            // difference check.
            boolean updated;
            if (stationMap.containsKey(station.sameAs)) {
                Station stored = stationMap.get(station.sameAs);
                // WeakHashが異なるときはStationデータの更新を行う
                updated = !(station.getUpdateHashcode().equals(stored.getUpdateHashcode()));
            } else {
                updated = true;
            }

            if (updated) {
                LOGI(TAG, "update station. " + station.sameAs);
                stationMap.put(station.sameAs, station);
                station.storeRecord();
                EventBus.getDefault().post(new UpdateStationEvent(station.sameAs));
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    void updateTimetable(StationTimetable[] timetables) {
        if (ArrayUtils.isEmpty(timetables)) {
            LOGW(TAG, "update timetable canceled(empty items).");
            return;
        }

        LOGI(TAG, "update timetable " + timetables.length + " items.");
        try {
            LOCK.writeLock().lock();
            LOGI(TAG, "  Timetable ID=" + timetables[0].sameAs);
            timetableLruCache.put(timetables[0].station, timetables);
            EventBus.getDefault().post(new UpdateTimetableEvent(timetables[0].station));

            // station update.
            Station relationStation = findStation(timetables[0].station);
            if (relationStation != null) {
                relationStation.timetable = timetables;
                EventBus.getDefault().post(new UpdateTimetableEvent(relationStation.sameAs));
            }
        } finally {
            LOCK.writeLock().unlock();
        }
        LOGD(TAG, "timetable timetable end.");
    }

    public void initialize() {
        try {
            LOCK.writeLock().lock();
            // Stationデータの初期化
            stationMap.clear();
            List<Station> stations = new Select().from(Station.class).execute();
            for (Station station : stations) {
                stationMap.put(station.sameAs, station);
            }

            // Railwayデータの初期化
            railwayMap.clear();
            List<Railway> railways = new Select().from(Railway.class).execute();
            for (Railway railway : railways) {
                railwayMap.put(railway.sameAs, railway);
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public static final class UpdateStationEvent {
        public final String sameAs;

        UpdateStationEvent(String sameAs) {
            this.sameAs = sameAs;
        }
    }

    public static final class UpdateRailwayEvent {
        public final String sameAs;

        UpdateRailwayEvent(String sameAs) {
            this.sameAs = sameAs;
        }
    }


    public static final class UpdateTimetableEvent {
        public final String stationSameAs;

        UpdateTimetableEvent(String stationSameAs) {
            this.stationSameAs = stationSameAs;
        }
    }

    private static final ContentRepository instance = new ContentRepository();

    public static ContentRepository get() {
        return instance;
    }

    private ContentRepository() { /* singleton */ }
}
