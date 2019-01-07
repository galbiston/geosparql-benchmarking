/**
 * Copyright 2018 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package data_setup;

/**
 *
 *
 */
public interface GraphURI {

    public static final String GADM_URI = "http://geographica.di.uoa.gr/dataset/gag";
    public static final String LGD_URI = "http://geographica.di.uoa.gr/dataset/lgd";
    public static final String GEONAMES_URI = "http://geographica.di.uoa.gr/dataset/geonames";
    public static final String HOTSPOTS_URI = "http://geographica.di.uoa.gr/dataset/hotspots";
    public static final String CLC_URI = "http://geographica.di.uoa.gr/dataset/clc";
    public static final String DBPEDIA_URI = "http://geographica.di.uoa.gr/dataset/dbpedia";
    public static final String DEFAULT = "";
    public static final String USER = "http://example.org/user/dataset#";
    public static final String CONFORMANCE_URI = "http://example.org/dataset#conformance";
    public static final String CONFORMANCE_EQUALS_URI = "http://example.org/dataset#conformance-equals";
}
