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

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class GsonAndUtf8ResponseRequest<T> extends GsonRequest<T> {
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonAndUtf8ResponseRequest(String url, Class<T> clazz, Map<String, String> headers,
                                      Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(url, clazz, headers, listener, errorListener);
    }

    @Override
    protected String onInterceptParseNetworkResponse(NetworkResponse response) throws UnsupportedEncodingException {
        return new String(response.data, HTTP.UTF_8);
    }
}
