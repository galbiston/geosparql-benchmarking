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
package io.github.galbiston.data_setup;

import java.io.File;
import java.util.TreeMap;

/**
 *
 *
 */
public class Dataset_CRS84 {

    public static final File FOLDER = new File("../datasets_CRS84");

    public static TreeMap<String, File> getAll() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.GADM_URI, new File(FOLDER, "gag.nt"));
        datasetMap.put(GraphURI.LGD_URI, new File(FOLDER, "linkedgeodata.nt"));
        datasetMap.put(GraphURI.GEONAMES_URI, new File(FOLDER, "geonames.nt"));
        datasetMap.put(GraphURI.HOTSPOTS_URI, new File(FOLDER, "hotspots.nt"));
        datasetMap.put(GraphURI.CLC_URI, new File(FOLDER, "corine.nt"));
        datasetMap.put(GraphURI.DBPEDIA_URI, new File(FOLDER, "dbpedia.nt"));
        return datasetMap;
    }

    public static TreeMap<String, File> getLinkedGeodata() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.LGD_URI, new File(FOLDER, "linkedgeodata.nt"));
        return datasetMap;
    }

    public static TreeMap<String, File> getGeonames() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.GEONAMES_URI, new File(FOLDER, "geonames.nt"));
        return datasetMap;
    }

    public static TreeMap<String, File> getSynthetic() {
        TreeMap<String, File> datasetMap = new TreeMap<>();
        datasetMap.put(GraphURI.DEFAULT, new File(FOLDER, "generator_512.nt"));
        return datasetMap;
    }

}
