/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

/**
 *
 *
 */
public class IterationResult {

    private final String testSystemName;
    private final String queryName;
    private final String queryString;
    private final int iteration;
    private final QueryResult queryResult;

    public IterationResult(String testSystemName, String queryName, String queryString, int iteration, QueryResult queryResult) {
        this.testSystemName = testSystemName;
        this.queryName = queryName;
        this.queryString = queryString;
        this.iteration = iteration;
        this.queryResult = queryResult;
    }

    public String getTestSystemName() {
        return testSystemName;
    }

    public String getQueryName() {
        return queryName;
    }

    public String getQueryString() {
        return queryString;
    }

    public int getIteration() {
        return iteration;
    }

    public QueryResult getQueryResult() {
        return queryResult;
    }

    @Override
    public String toString() {
        return "IterationResult{" + "testSystemName=" + testSystemName + ", queryName=" + queryName + ", queryString=" + queryString + ", iteration=" + iteration + ", queryResult=" + queryResult + '}';
    }

}
