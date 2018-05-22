/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.parliament;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import execution.QueryTask;
import execution_results.QueryResult;
import execution_results.VarValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class ParliamentQueryTask implements QueryTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String queryString;
    private final Dataset dataset;
    private QueryResult queryResult;

    public ParliamentQueryTask(String queryString, Dataset dataset) {
        this.queryString = queryString;
        this.dataset = dataset;
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
        QueryExecution qexec = null;
        try {
            qexec = QueryExecutionFactory.create(queryString, dataset);
            ResultSet rs = qexec.execSelect();
            queryNanoTime = System.nanoTime();
            while (rs.hasNext()) {
                QuerySolution querySolution = rs.next();
                Iterator<String> varNames = querySolution.varNames();
                List<VarValue> result = new ArrayList<>();
                while (varNames.hasNext()) {
                    String varName = varNames.next();
                    String valueStr;
                    RDFNode solution = querySolution.get(varName);
                    if (solution.isLiteral()) {
                        Literal literal = solution.asLiteral();
                        valueStr = literal.getLexicalForm();
                    } else if (solution.isResource()) {
                        Resource resource = solution.asResource();
                        valueStr = resource.getURI();
                    } else {
                        Node anon = solution.asNode();
                        valueStr = anon.getBlankNodeLabel();
                        LOGGER.error("Anon Node result: " + valueStr);
                    }
                    VarValue varValue = new VarValue(varName, valueStr);
                    result.add(varValue);
                }
                results.add(result);
            }
        } catch (Exception ex) {
            LOGGER.error("Thread Exception: {}", ex.getMessage());
            queryNanoTime = startNanoTime;
            results.clear();
            isComplete = false;
        } finally {
            if (qexec != null) {
                qexec.close();
            }
        }
        long resultsNanoTime = System.nanoTime();
        this.queryResult = new QueryResult(startNanoTime, queryNanoTime, resultsNanoTime, results, isComplete);
        LOGGER.info("Query Evaluation Time - Start->Query: {}, Query->Results: {}, Start->Results: {}", queryResult.getStartQueryDuration(), queryResult.getQueryResultsDuration(), queryResult.getStartResultsDuration());
    }
}
