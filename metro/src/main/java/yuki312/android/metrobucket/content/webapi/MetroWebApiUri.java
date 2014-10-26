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

import android.net.Uri;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public abstract class MetroWebApiUri {
    public static final String ENDPOINT = "https://api.tokyometroapp.jp/api/v2/";

    private ArrayList<String> paths = new ArrayList<String>();
    private HashMap<RdfProperty, String> parameters = new HashMap<RdfProperty, String>();

    public MetroWebApiUri appendPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            throw new IllegalArgumentException("path can't be null.");
        }
        paths.add(path);
        return this;
    }

    public MetroWebApiUri putParam(RdfProperty prop, String str) {
        if (prop == null || Strings.isNullOrEmpty(str)) {
            throw new IllegalArgumentException("RDF property or value can't be null.");
        }
        parameters.put(prop, str);
        return this;
    }

    public Uri build() {
        Uri.Builder uriBuilder = Uri.parse(getEndpoint()).buildUpon();

        // add paths.
        for (String path: this.paths) {
            uriBuilder.appendPath(path);
        }

        // add parameters.
        Set<RdfProperty> props = parameters.keySet();
        for (RdfProperty propName : props) {
            String propVal = parameters.get(propName);
            uriBuilder.appendQueryParameter(propName.label(), propVal);
        }

        // add consumer key.
        String consumerKey = MetroConsumerKey.get();
        uriBuilder.appendQueryParameter(RdfProperty.Acl.consumerKey.label(), consumerKey);

        return uriBuilder.build();
    }

    protected abstract String getEndpoint();


    /**
     * Datapoints: https://api.tokyometroapp.jp/api/v2/datapoints
     */
    public static class Datapoints extends MetroWebApiUri {
        public static final String ENDPOINT = MetroWebApiUri.ENDPOINT + "datapoints/";

        @Override
        public String getEndpoint() {
            return ENDPOINT;
        }
    }


    /**
     * Places: https://api.tokyometroapp.jp/api/v2/places
     */
    public static class Places extends MetroWebApiUri {
        public static final String ENDPOINT = MetroWebApiUri.ENDPOINT + "places/";

        @Override
        public String getEndpoint() {
            return ENDPOINT;
        }
    }

    public static class Endpoint extends MetroWebApiUri {
        public final String ENDPOINT;

        public Endpoint(String uri) {
            if (Strings.isNullOrEmpty(uri)) {
                throw new IllegalArgumentException("Endpoint can not be null.");
            }
            this.ENDPOINT = uri;
        }
        @Override
        protected String getEndpoint() {
            return ENDPOINT;
        }
    }
}
