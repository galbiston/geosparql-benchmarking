/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

import execution_results.QueryResult;
import execution_results.VarValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public abstract class QueryTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final QueryResult getQueryResult() {
        return queryResult;
    }

    protected abstract void prepareQuery() throws Exception;

    /**
     * Retrieve the results from the prepared query and place in the results
     * list.
     *
     * @throws java.lang.Exception
     */
    protected abstract void executeQuery() throws Exception;

    protected abstract void endQuery();

    private QueryResult queryResult = new QueryResult();
    protected final List<List<VarValue>> results = new ArrayList<>();

    @Override
    public final void run() {

        Boolean isComplete = true;
        long startNanoTime = System.nanoTime();
        long queryNanoTime;
        try {
            prepareQuery();
            queryNanoTime = System.nanoTime();
            executeQuery();
        } catch (Exception ex) {
            LOGGER.error("Thread Exception: {}", ex.getMessage());
            queryNanoTime = startNanoTime;
            results.clear();
            isComplete = false;
        } finally {
            endQuery();
        }

        long resultsNanoTime = System.nanoTime();

        this.queryResult = new QueryResult(startNanoTime, queryNanoTime, resultsNanoTime, results, isComplete);
        LOGGER.info("Query Evaluation Time - Start->Query: {}, Query->Results: {}, Start->Results: {}", queryResult.getStartQueryDuration(), queryResult.getQueryResultsDuration(), queryResult.getStartResultsDuration());
    }

}
