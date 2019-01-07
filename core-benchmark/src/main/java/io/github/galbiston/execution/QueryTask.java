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
package io.github.galbiston.execution;

import io.github.galbiston.execution_results.QueryResult;
import io.github.galbiston.execution_results.VarValue;
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
