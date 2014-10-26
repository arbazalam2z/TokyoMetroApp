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

public interface RdfProperty {
    // JSON-LD意外に特化させたい場合はWrapperとFactoryを用意する.

    public String label();

    public enum _At implements RdfProperty {
        context("@context"),
        id("@id"),
        type("@type");

        private final String label;

        private _At(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Rdf implements RdfProperty {
        type("rdf:type");

        private final String label;

        private Rdf(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Acl implements RdfProperty {
        consumerKey("acl:consumerKey");

        private final String label;

        private Acl(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Odpt implements RdfProperty {
        connectingrailway("odpt:connectingRailway"),
        exit("odpt:exit"),
        facility("odpt:facility"),
        operator("odpt:operator"),
        passengerSurvey("odpt:passengerSurvey"),
        railway("odpt:railway"),
        station("odpt:station"),
        stationCode("odpt:stationCode"),
        stationTimetable("odpt:stationTimetable"),
        stationFacility("odpt:stationFacility"),
        train("odpt:train"),
        trainInformation("odpt:trainInformation");

        private final String label;

        private Odpt(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Owl implements RdfProperty {
        sameAs("owl:sameAs");

        private final String label;

        private Owl(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Ug implements RdfProperty {
        poi("ug:poi"),
        region("ug:region");

        private final String label;

        private Ug(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum Mlit implements RdfProperty {
        station("mlit:station"),
        railway("mlit:railway");

        private final String label;

        private Mlit(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }
}
