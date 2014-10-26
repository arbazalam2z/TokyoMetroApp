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

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import static yuki312.android.metrobucket.util.LogUtils.LOGD;
import static yuki312.android.metrobucket.util.LogUtils.LOGW;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

public class JsonFetcher<T> {
    private static final String TAG = makeLogTag(JsonFetcher.class);

    private static RequestQueue requestQueue;

    /**
     * GsonFetcherを初期化する.
     * この操作はRequestQueueの生成も行う.
     *
     * @param context RequestQueueを生成するContext. パッケージ情報参照用でキャッシュされない.
     */
    public static void initialize(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    public void fetch(String url, Class<T> clazz, FetchListener fetchListener) {
        VolleyFetchListener<T> listener = new VolleyFetchListener<T>(fetchListener);
        GsonRequest<T> gsonRequest = new GsonAndUtf8ResponseRequest<T>(url, clazz, null, listener, listener);
        requestQueue.add(gsonRequest);
    }

    private class VolleyFetchListener<T> implements Response.Listener<T>, Response.ErrorListener {
        private final FetchListener<T> listener;

        private VolleyFetchListener(FetchListener listener) {
            this.listener = (listener == null ? NULL_FETCH_LISTENER : listener);
        }

        @Override
        public void onResponse(T result) {
            this.listener.onJsonFetched(result);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            LOGW(TAG, "onErrorResponse. " + error.toString());
            this.listener.onJsonError();
        }
    }

    public interface FetchListener<T> {
        public void onJsonFetched(T result);

        public void onJsonError();
    }

    private static final FetchListener NULL_FETCH_LISTENER = new FetchListener() {
        @Override
        public void onJsonFetched(Object result) {
        }

        @Override
        public void onJsonError() {
        }
    };
}
