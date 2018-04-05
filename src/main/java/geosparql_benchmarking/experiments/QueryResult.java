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
    private final Duration startQueryDuration;
    private final Duration queryResultsDuration;
    private final Duration totalElapsedDuration;
    private final List<HashMap<String, String>> results;
    private final boolean isCompleted;

    /**
     * Provides a QueryResult that stores the three time checks, results and
     * whether the query completed successfully.
     *
     * @param startNanoTime
     * @param queryNanoTime
     * @param resultsNanoTime
     * @param results
     * @param isCompleted
     */
    public QueryResult(long startNanoTime, long queryNanoTime, long resultsNanoTime, List<HashMap<String, String>> results, boolean isCompleted) {
        this.startNanoTime = startNanoTime;
        this.queryNanoTime = queryNanoTime;
        this.resultsNanoTime = resultsNanoTime;
        this.startQueryDuration = Duration.ofNanos(queryNanoTime - startNanoTime);
        this.queryResultsDuration = Duration.ofNanos(resultsNanoTime - queryNanoTime);
        this.totalElapsedDuration = Duration.ofNanos(resultsNanoTime - startNanoTime);
        this.results = results;
        this.isCompleted = isCompleted;
    }

    /**
     * Provides a QueryResult with no query time and used for Update tasks.
     *
     * @param startNanoTime
     * @param resultsNanoTime
     * @param isCompleted
     */
    public QueryResult(long startNanoTime, long resultsNanoTime, boolean isCompleted) {
        this(startNanoTime, startNanoTime, resultsNanoTime, new ArrayList<>(), isCompleted);
    }

    /**
     * Provides a default failure QueryResult.
     */
    public QueryResult() {
        this(0, 0, false);
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

    public Duration getStartQueryDuration() {
        return startQueryDuration;
    }

    public Duration getQueryResultsDuration() {
        return queryResultsDuration;
    }

    public Duration getTotalElapsedDuration() {
        return totalElapsedDuration;
    }

    public List<HashMap<String, String>> getResults() {
        return results;
    }

    public int getCount() {
        return results.size();
    }

    @Override
    public String toString() {
        return "QueryResult{" + "startNanoTime=" + startNanoTime + ", queryNanoTime=" + queryNanoTime + ", resultsNanoTime=" + resultsNanoTime + ", startQueryDuration=" + startQueryDuration + ", queryResultsDuration=" + queryResultsDuration + ", totalElapsedDuration=" + totalElapsedDuration + ", results=" + results + ", isCompleted=" + isCompleted + '}';
    }

}
