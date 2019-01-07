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
package io.github.galbiston.geosparql_benchmarking.execution;

import io.github.galbiston.geosparql_benchmarking.data_setup.Dataset_CRS84;
import io.github.galbiston.geosparql_benchmarking.data_setup.Dataset_Conformance;
import io.github.galbiston.geosparql_benchmarking.data_setup.Dataset_Greek_Grid;
import io.github.galbiston.geosparql_benchmarking.data_setup.Dataset_WGS84;
import io.github.galbiston.geosparql_benchmarking.data_setup.Dataset_WGS84_Legacy;
import java.io.File;
import java.util.TreeMap;

/**
 *
 *
 */
public class DatasetInfo {

    private String datasetName;
    private TreeMap<String, File> datasetMap;

    public DatasetInfo(String datasetName, TreeMap<String, File> datasetMap) {
        this.datasetName = datasetName;
        this.datasetMap = datasetMap;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public TreeMap<String, File> getDatasetMap() {
        return datasetMap;
    }

    @Override
    public String toString() {
        return "DatasetInfo{" + "datasetName=" + datasetName + ", datasetMap=" + datasetMap + '}';
    }

    public static final DatasetInfo WGS_84 = new DatasetInfo("WGS84", Dataset_WGS84.getAll());
    public static final DatasetInfo GREEK_GRID = new DatasetInfo("GreekGrid", Dataset_Greek_Grid.getAll());
    public static final DatasetInfo WGS_84_LEGACY = new DatasetInfo("WG84 Legacy", Dataset_WGS84_Legacy.getAll());
    public static final DatasetInfo CRS_84 = new DatasetInfo("CRS84", Dataset_CRS84.getAll());
    public static final DatasetInfo CONFORMANCE = new DatasetInfo("Conformance", Dataset_Conformance.getConformanceData());

}
