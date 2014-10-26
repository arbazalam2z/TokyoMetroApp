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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import static yuki312.android.metrobucket.util.LogUtils.LOGD;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

public class SyncStationService extends IntentService {
    private static final String TAG = makeLogTag(SyncStationService.class);

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SYNC_STAION = "yuki312.android.metrobucket.content.action.SYNC_STAION";
    private static final String ACTION_SYNC_STAION_ALL = "yuki312.android.metrobucket.content.action.SYNC_STAION_ALL";

    private static final String EXTRA_SYNC_PARAM_STAIONID = "yuki312.android.metrobucket.content.extra.SYNC_PARAM_STAIONID";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startSyncAt(Context context, String stationId) {
        Intent intent = new Intent(context, SyncStationService.class);
        intent.setAction(ACTION_SYNC_STAION);
        intent.putExtra(EXTRA_SYNC_PARAM_STAIONID, stationId);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startSyncAll(Context context) {
        Intent intent = new Intent(context, SyncStationService.class);
        intent.setAction(ACTION_SYNC_STAION_ALL);
        context.startService(intent);
    }

    public SyncStationService() {
        super("SyncStationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNC_STAION.equals(action)) {
                final String stationId = intent.getStringExtra(EXTRA_SYNC_PARAM_STAIONID);
                handleSyncStationAt(stationId);
            } else if (ACTION_SYNC_STAION_ALL.equals(action)) {
                handleSyncStationAll();
            }
        }
    }

    private void handleSyncStationAt(String stationId) {
        LOGD(TAG, "handleSyncStationAt handled.");
        ContentFetcher.get().fetchStationAt(stationId);
    }

    private void handleSyncStationAll() {
        LOGD(TAG, "SyncStationAll handled.");
        ContentFetcher.get().fetchAllStation();
    }

    void testHandleSyncStationAt(String stationId) {
        this.handleSyncStationAt(stationId);
    }

    void testHandleSyncStationAll() {
        this.handleSyncStationAll();
    }
}
