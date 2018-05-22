/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import eu.earthobservatory.utils.Format;
import execution.QueryTask;
import execution_results.QueryResult;
import execution_results.VarValue;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class StrabonQueryTask implements QueryTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String query;
    private final Strabon strabon;
    private QueryResult queryResult;

    public StrabonQueryTask(String query, Strabon strabon) {
        this.query = query;
        this.strabon = strabon;
        this.queryResult = new QueryResult();
    }

    @Override
    public QueryResult getQueryResult() {
        return queryResult;
    }

    @Override
    public void run() {

        LOGGER.info("Query Evaluation: Started");
        Boolean isComplete = true;
        List<List<VarValue>> results = new ArrayList<>();
        long startNanoTime = System.nanoTime();
        long queryNanoTime;
        try {

            TupleQuery tupleQuery = (TupleQuery) strabon.query(query, Format.TUQU, strabon.getSailRepoConnection(), System.out);

            TupleQueryResult tupleQueryResult = tupleQuery.evaluate();
            queryNanoTime = System.nanoTime();

            while (tupleQueryResult.hasNext()) {

                BindingSet bindingSet = tupleQueryResult.next();
                List<String> bindingNames = tupleQueryResult.getBindingNames();
                List<VarValue> result = new ArrayList<>();
                for (String binding : bindingNames) {
                    Value value = bindingSet.getValue(binding);
                    String valueStr = value.stringValue();
                    VarValue varValue = new VarValue(binding, valueStr);
                    result.add(varValue);
                }
                results.add(result);
            }
            tupleQueryResult.close();

        } catch (MalformedQueryException | QueryEvaluationException | TupleQueryResultHandlerException | IOException ex) {
            LOGGER.error("Thread Exception: {}", ex.getMessage());
            queryNanoTime = startNanoTime;
            results.clear();
            isComplete = false;
        }
        long resultsNanoTime = System.nanoTime();

        this.queryResult = new QueryResult(startNanoTime, queryNanoTime, resultsNanoTime, results, isComplete);
        LOGGER.info("Query Evaluation Time - Start->Query: {}, Query->Results: {}, Start->Results: {}", queryResult.getStartQueryDuration(), queryResult.getQueryResultsDuration(), queryResult.getStartResultsDuration());
    }

}
