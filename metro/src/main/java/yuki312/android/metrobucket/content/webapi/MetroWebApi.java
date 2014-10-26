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

package yuki312.android.metrobucket.content.webapi;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import yuki312.android.metrobucket.content.net.JsonFetcher;
import yuki312.android.metrobucket.content.net.PeriodicJsonFetcher;

import static yuki312.android.metrobucket.util.LogUtils.LOGD;
import static yuki312.android.metrobucket.util.LogUtils.LOGW;
import static yuki312.android.metrobucket.util.LogUtils.makeLogTag;

/**
 * メトロWebAPIの基底クラス.
 * 東京メトロWebAPIへのアクセス制御など共通処理はここに集約され, 全てのWebAPIへの制約となる.
 * 制約リスト
 * - WebAPIのアクセス頻度はxxxms毎でなければいけない.
 */
public abstract class MetroWebApi<T> implements JsonFetcher.FetchListener<T> {
    private static final String TAG = makeLogTag(MetroWebApi.class);

    protected void call(String url, Class<T> clazz) {
        LOGD(TAG, url);
        PeriodicJsonFetcher fetcher = new PeriodicJsonFetcher();
        fetcher.fetch(url, clazz, this);
    }

    @SuppressWarnings("unchecked")
    private WebApiListener<T> listener = WebApiListener.EMPTY;

    public void listen(WebApiListener<T> listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    @Override
    public void onJsonFetched(T result) {
        this.listener.onResponse(new ApiResponse<T>(ApiResponse.Status.Success, result));
    }

    @Override
    public void onJsonError() {
        this.listener.onResponse(new ApiResponse<T>(ApiResponse.Status.Error, null));
    }

    public static class ApiResponse<T> {
        public enum Status {
            Success, Cancel, Error
        }

        public final Status code;
        public final T obj;

        protected ApiResponse(Status code, T response) {
            this.code = code;
            this.obj = response;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }

    public interface WebApiListener<T> {
        public static final WebApiListener EMPTY = new WebApiListener() {
            @Override
            public void onResponse(ApiResponse response) {
                LOGD(TAG, "onResponse<EMPTY>.");
            }
        };

        public void onResponse(ApiResponse<T> response);
    }
}
