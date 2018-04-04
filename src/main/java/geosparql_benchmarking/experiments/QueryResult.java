/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

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
    private final long startQueryNanoTime;
    private final long queryResultsNanoTime;
    private final long totalElapsedNanoTime;
    private final List<HashMap<String, String>> results;
    private final boolean isCompleted;

    public QueryResult(long startNanoTime, long queryNanoTime, long resultsNanoTime, List<HashMap<String, String>> results, boolean isCompleted) {
        this.startNanoTime = startNanoTime;
        this.queryNanoTime = queryNanoTime;
        this.resultsNanoTime = resultsNanoTime;
        this.startQueryNanoTime = queryNanoTime - startNanoTime;
        this.queryResultsNanoTime = resultsNanoTime - queryNanoTime;
        this.totalElapsedNanoTime = resultsNanoTime - startNanoTime;
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

    public long getStartQueryNanoTime() {
        return startQueryNanoTime;
    }

    public long getQueryResultsNanoTime() {
        return queryResultsNanoTime;
    }

    public long getTotalElapsedNanoTime() {
        return totalElapsedNanoTime;
    }

    public List<HashMap<String, String>> getResults() {
        return results;
    }

    public int getCount() {
        return results.size();
    }

    @Override
    public String toString() {
        return "QueryResult{" + "startNanoTime=" + startNanoTime + ", queryNanoTime=" + queryNanoTime + ", resultsNanoTime=" + resultsNanoTime + ", startQueryNanoTime=" + startQueryNanoTime + ", queryResultsNanoTime=" + queryResultsNanoTime + ", totalElapsedNanoTime=" + totalElapsedNanoTime + ", results=" + results + ", isCompleted=" + isCompleted + '}';
    }

}
