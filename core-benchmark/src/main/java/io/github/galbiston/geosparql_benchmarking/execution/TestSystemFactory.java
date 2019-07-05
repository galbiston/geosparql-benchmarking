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

import io.github.galbiston.geosparql_benchmarking.execution_results.DatasetLoadResult;
import java.io.File;
import java.util.TreeMap;

/**
 *
 * @author Gerg
 */
public interface TestSystemFactory {

    public TestSystem getTestSystem();

    public String getTestSystemName();

    public File getResultsFolder();

    /**
     * Loads the dataset into the target location. No clearing of the dataset is
     * done before this and may be recommended.
     *
     * @param datasetMap
     * @param iteration
     * @return Result of dataset load.
     */
    public DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, Integer iteration);

    /**
     * Clears the entire contents of the dataset.
     *
     * @return If dataset clearance successful.
     */
    public Boolean clearDataset();

    /**
     * Clear the entire contents of the dataset before loading fresh data.
     *
     * @param datasetMap
     * @return If dataset clearance successful.
     */
    public Boolean clearLoadDataset(TreeMap<String, File> datasetMap);

    /**
     * Clear the entire contents of the dataset before loading fresh data.
     *
     * @param datasetFileName
     * @return If dataset clearance successful.
     */
    public void loadDataset(String datasetFileName);
}
