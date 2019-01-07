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
package execution_results;

import java.time.Duration;

/**
 *
 *
 */
public class DatasetLoadTimeResult {

    private final String datasetName;
    private final long startNanoTime;
    private final long endNanoTime;
    private final Duration startEndDuration;

    public DatasetLoadTimeResult(String datasetName, long startNanoTime, long endNanoTime) {
        this.datasetName = datasetName;
        this.startNanoTime = startNanoTime;
        this.endNanoTime = endNanoTime;
        this.startEndDuration = Duration.ofNanos(endNanoTime - startNanoTime);
    }

    public String getDatasetName() {
        return datasetName;
    }

    public long getStartNanoTime() {
        return startNanoTime;
    }

    public long getEndNanoTime() {
        return endNanoTime;
    }

    public Duration getStartEndDuration() {
        return startEndDuration;
    }

    @Override
    public String toString() {
        return "DatasetLoadTimeResult{" + "datasetName=" + datasetName + ", startNanoTime=" + startNanoTime + ", endNanoTime=" + endNanoTime + ", startEndDuration=" + startEndDuration + '}';
    }

}
