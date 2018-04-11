/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

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
