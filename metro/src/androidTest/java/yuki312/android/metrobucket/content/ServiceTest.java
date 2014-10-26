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

import android.content.Intent;
import android.test.ServiceTestCase;

import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;
import yuki312.android.metrobucket.content.model.Station;
import yuki312.android.metrobucket.content.net.JsonFetcher;
import yuki312.android.metrobucket.content.webapi.MetroConsumerKey;

import static yuki312.android.metrobucket.util.LogUtils.LOGE;
import static yuki312.android.metrobucket.util.LogUtils.LOGW;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

public class ServiceTest extends ServiceTestCase<SyncStationService> {
    private static final String TAG = makeLogTag(ServiceTest.class);

    private CountDownLatch latch;

    public ServiceTest() {
        super(SyncStationService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        JsonFetcher.initialize(getContext());
        MetroConsumerKey.initialize(getContext());
    }

    public void testSyncService() {
        makeLatch(185*2);  // FIXME: 全駅数が取得可能であればその値を指定. 2014.10.11 現在185駅

        FetchStationEventListener listener = new FetchStationEventListener();
        eventbusRegister(listener);
        Intent intent = new Intent(getContext(), SyncStationService.class);
        startService(intent);
        SyncStationService service = getService();
        service.testHandleSyncStationAll();

        waitLatch();

        String[] sameAsSet = ContentRepository.get().stationSameAsSets();
        for (String sameAs : sameAsSet) {
            LOGE(TAG, "IDs=" + sameAs);
            Station station = ContentRepository.get().findStation(sameAs);
            LOGW(TAG, "  station detail=" + station.toString());
        }
        eventbusUnregister(listener);
    }


    private void countDown() {
        this.latch.countDown();
    }

    private void makeLatch(int count) {
        latch = new CountDownLatch(count);
    }

    private void waitLatch() {
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void eventbusRegister(FetchStationEventListener listener) {
        EventBus.getDefault().register(listener);
    }

    private void eventbusUnregister(FetchStationEventListener listener) {
        EventBus.getDefault().unregister(listener);
    }

    interface EventBusListener<T> {
        public void onEvent(T event);
    }

    private class FetchStationEventListener implements EventBusListener<ContentRepository.UpdateStationEvent> {
        @Override
        public void onEvent(ContentRepository.UpdateStationEvent event) {
            LOGW(TAG, this.toString() + "::" + event.toString());
            countDown();
        }
    }
}
