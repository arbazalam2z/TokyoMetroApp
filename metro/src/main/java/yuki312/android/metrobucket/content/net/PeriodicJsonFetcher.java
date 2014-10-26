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

package yuki312.android.metrobucket.content.net;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

public class PeriodicJsonFetcher {
    private static final String TAG = makeLogTag(PeriodicJsonFetcher.class);

    private static ScheduledExecutorService fetchScheduler = Executors.newSingleThreadScheduledExecutor();
    private static Queue<FetchJob> jobQueue = new ConcurrentLinkedQueue<FetchJob>();
    private static final Object QUEUE_LOCK = new Object();

    private Runnable jobExecutor = new Runnable() {
        @Override
        public void run() {
            synchronized (QUEUE_LOCK) {
                FetchJob job = jobQueue.poll();
                new JsonFetcher().fetch(job.url, job.clazz, job.listener);
            }
            nextPeriod();
        }
    };

    public void fetch(String url, Class clazz, JsonFetcher.FetchListener fetchListener) {
        synchronized (QUEUE_LOCK) {
            jobQueue.add(new FetchJob(url, clazz, fetchListener));
            if (jobQueue.size() == 1 /* first attach */) {
                nextPeriod();
            }
        }
    }

    private void nextPeriod() {
        synchronized (QUEUE_LOCK) {
            if (!jobQueue.isEmpty()) {
                fetchScheduler.schedule(jobExecutor, 250, TimeUnit.MILLISECONDS);
            }
        }
    }

    private static class FetchJob {
        private final String url;
        private final Class clazz;
        private final JsonFetcher.FetchListener listener;

        private FetchJob(String url, Class clazz, JsonFetcher.FetchListener listener) {
            this.url = url;
            this.clazz = clazz;
            this.listener = listener;
        }
    }
}
