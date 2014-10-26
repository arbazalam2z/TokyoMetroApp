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

package yuki312.android.metrobucket;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import yuki312.android.metrobucket.content.ContentRepository;
import yuki312.android.metrobucket.content.model.Railway;
import yuki312.android.metrobucket.content.model.Station;
import yuki312.android.metrobucket.content.net.JsonFetcher;
import yuki312.android.metrobucket.content.webapi.MetroConsumerKey;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // ActiveAndroid
        Configuration.Builder builder = new Configuration.Builder(this);
        builder.setDatabaseName("test.db");
        builder.setDatabaseVersion(1);
        builder.addModelClass(Station.class);
        builder.addModelClass(Railway.class);
        ActiveAndroid.initialize(builder.create(), true);

        // Basic modules.
        MetroConsumerKey.initialize(this.getApplicationContext());
        JsonFetcher.initialize(this.getApplicationContext());

        // Controller modules.
        ContentRepository.get().initialize();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
