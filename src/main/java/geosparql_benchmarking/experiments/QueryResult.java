/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 *
 */
public class QueryResult {

    private final long startNanoTime;
    private final long queryNanoTime;
    private final long resultsNanoTime;
    private final Duration startQueryTime;
    private final Duration queryResultsTime;
    private final Duration totalElapsedTime;
    private final List<HashMap<String, String>> results;
    private final boolean isCompleted;

    public QueryResult(long startNanoTime, long queryNanoTime, long resultsNanoTime, List<HashMap<String, String>> results, boolean isCompleted) {
        this.startNanoTime = startNanoTime;
        this.queryNanoTime = queryNanoTime;
        this.resultsNanoTime = resultsNanoTime;
        this.startQueryTime = Duration.ofNanos(queryNanoTime - startNanoTime);
        this.queryResultsTime = Duration.ofNanos(resultsNanoTime - queryNanoTime);
        this.totalElapsedTime = Duration.ofNanos(resultsNanoTime - startNanoTime);
        this.results = results;
        this.isCompleted = isCompleted;
    }

    public QueryResult(long startNanoTime, long resultsNanoTime, boolean isCompleted) {
        this(startNanoTime, startNanoTime, resultsNanoTime, new ArrayList<>(), isCompleted);
    }

    public long getStartNanoTime() {
        return startNanoTime;
    }

    public long getQueryNanoTime() {
        return queryNanoTime;
    }

    public long getResultsNanoTime() {
        return resultsNanoTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public Duration getStartQueryTime() {
        return startQueryTime;
    }

    public Duration getQueryResultsTime() {
        return queryResultsTime;
    }

    public Duration getTotalElapsedTime() {
        return totalElapsedTime;
    }

    public List<HashMap<String, String>> getResults() {
        return results;
    }

    public int getCount() {
        return results.size();
    }

    @Override
    public String toString() {
        return "QueryResult{" + "startNanoTime=" + startNanoTime + ", queryNanoTime=" + queryNanoTime + ", resultsNanoTime=" + resultsNanoTime + ", startQueryTime=" + startQueryTime + ", queryResultsTime=" + queryResultsTime + ", totalElapsedTime=" + totalElapsedTime + ", results=" + results + ", isCompleted=" + isCompleted + '}';
    }

}
