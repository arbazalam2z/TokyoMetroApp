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

/**
 * Created by yuki on 2014/10/19.
 */
public class MetroConst {
    public enum RailwayConst {
        MarunouchiBranch("odpt.Railway:TokyoMetro.MarunouchiBranch"),
        Ginza("odpt.Railway:TokyoMetro.Ginza"),
        Marunouchi("odpt.Railway:TokyoMetro.Marunouchi"),
        Hibiya("odpt.Railway:TokyoMetro.Hibiya"),
        Tozai("odpt.Railway:TokyoMetro.Tozai"),
        Chiyoda("odpt.Railway:TokyoMetro.Chiyoda"),
        Yurakucho("odpt.Railway:TokyoMetro.Yurakucho"),
        Hanzomon("odpt.Railway:TokyoMetro.Hanzomon"),
        Namboku("odpt.Railway:TokyoMetro.Namboku"),
        Fukutoshin("odpt.Railway:TokyoMetro.Fukutoshin");

        public final String sameAs;
        RailwayConst(String sameAs) {
            this.sameAs = sameAs;
        }
    }
}
