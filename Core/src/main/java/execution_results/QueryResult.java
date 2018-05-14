/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution_results;

import java.time.Duration;
import java.util.ArrayList;
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
    private final Duration startResultsDuration;
    private final List<List<VarValue>> results;
    private final Boolean isCompleted;
    private final Integer resultsVariableCount;
    private final List<String> resultsVariableLabels;

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
    public QueryResult(long startNanoTime, long queryNanoTime, long resultsNanoTime, List<List<VarValue>> results, Boolean isCompleted) {
        this.startNanoTime = startNanoTime;
        this.queryNanoTime = queryNanoTime;
        this.resultsNanoTime = resultsNanoTime;
        this.startQueryDuration = Duration.ofNanos(queryNanoTime - startNanoTime);
        this.queryResultsDuration = Duration.ofNanos(resultsNanoTime - queryNanoTime);
        this.startResultsDuration = Duration.ofNanos(resultsNanoTime - startNanoTime);
        this.results = results;
        this.isCompleted = isCompleted;

        if (!results.isEmpty()) {
            List<VarValue> result = results.get(0);
            this.resultsVariableCount = result.size();
            this.resultsVariableLabels = new ArrayList<>(resultsVariableCount);
            for (VarValue varValue : result) {
                this.resultsVariableLabels.add(varValue.getVar());
            }

        } else {
            this.resultsVariableCount = 0;
            this.resultsVariableLabels = new ArrayList<>();
        }
    }

    /**
     * Provides a QueryResult with no query time or results but completed. Used
     * for Update tasks.
     *
     * @param startNanoTime
     * @param resultsNanoTime
     */
    public QueryResult(long startNanoTime, long resultsNanoTime) {
        this(startNanoTime, startNanoTime, resultsNanoTime, new ArrayList<>(), true);
    }

    /**
     * Provides a default failure QueryResult.
     */
    public QueryResult() {
        this(0, 0);
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

    public Boolean isCompleted() {
        return isCompleted;
    }

    public Duration getStartQueryDuration() {
        return startQueryDuration;
    }

    public Duration getQueryResultsDuration() {
        return queryResultsDuration;
    }

    public Duration getStartResultsDuration() {
        return startResultsDuration;
    }

    public List<List<VarValue>> getResults() {
        return results;
    }

    public Integer getResultsCount() {
        return results.size();
    }

    public Integer getResultsVariableCount() {
        return resultsVariableCount;
    }

    public List<String> getResultsVariableLabels() {
        return resultsVariableLabels;
    }

    @Override
    public String toString() {
        return "QueryResult{" + "startNanoTime=" + startNanoTime + ", queryNanoTime=" + queryNanoTime + ", resultsNanoTime=" + resultsNanoTime + ", startQueryDuration=" + startQueryDuration + ", queryResultsDuration=" + queryResultsDuration + ", startResultsDuration=" + startResultsDuration + ", results=" + results + ", isCompleted=" + isCompleted + ", resultsVariableCount=" + resultsVariableCount + ", resultsVariableLabels=" + resultsVariableLabels + '}';
    }

}