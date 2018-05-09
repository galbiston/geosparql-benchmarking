/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.geosparql_jena;

import execution_results.QueryResult;
import execution.TestSystem;
import execution_results.VarValue;
import implementation.GeoSPARQLSupport;
import implementation.index.IndexOption;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeosparqlJenaTestSystem implements TestSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Dataset dataset;
    private IndexOption indexOption;

    public GeosparqlJenaTestSystem(File datasetFolder, IndexOption indexOption) {
        //Access the datset from the folder.
        setup(TDBFactory.createDataset(datasetFolder.getAbsolutePath()), indexOption);
    }

    public GeosparqlJenaTestSystem(Dataset dataset, IndexOption indexOption) {

        //Copy the contents of the dataset to a new memory dataset.
        Dataset memDataset = DatasetFactory.createTxnMem();
        Iterator<String> graphNames = dataset.listNames();
        while (graphNames.hasNext()) {
            String graphName = graphNames.next();
            Model model = ModelFactory.createDefaultModel();
            model.add(dataset.getNamedModel(graphName));
            memDataset.addNamedModel(graphName, model);
        }

        setup(memDataset, indexOption);
    }

    private void setup(Dataset dataset, IndexOption indexOption) {
        this.dataset = dataset;
        this.indexOption = indexOption;
        try {
            GeoSPARQLSupport.loadFunctions(indexOption);
            GeoSPARQLSupport.clearAllIndexesAndRegistries();
        } catch (Exception ex) {
            throw new AssertionError("Issue accessing GeosparqlJena library. " + ex.getMessage());
        }
    }

    @Override
    public QueryResult runQueryWithTimeout(String query, Duration timeout) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(1);
        QueryTask runnable = new QueryTask(query, dataset);
        Future<?> future = executor.submit(runnable);

        try {
            LOGGER.debug("GeoSPARQL Jena Future: Started");
            future.get(timeout.getSeconds(), TimeUnit.SECONDS);
            LOGGER.debug("GeoSPARQL Jena Future: Completed");
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        } finally {
            LOGGER.debug("GeoSPARQL Jena: Executor Shutdown");
            executor.shutdown();
            System.gc();
        }

        QueryResult queryResult = runnable.getQueryResult();
        return queryResult;

    }

    @Override
    public QueryResult runUpdate(String query) {
        LOGGER.info("GeoSPARQL Jena Update: Started");
        long startNanoTime = System.nanoTime();
        UpdateRequest updateRequest = UpdateFactory.create(query);
        UpdateAction.execute(updateRequest, dataset);
        long resultsNanoTime = System.nanoTime();
        LOGGER.info("GeoSPARQL Jena Update: Completed");

        return new QueryResult(startNanoTime, resultsNanoTime);
    }

    @Override
    public void close() {
        dataset.close();
        if (TDBFactory.isBackedByTDB(dataset)) {
            TDBFactory.release(dataset);
        }
        GeoSPARQLSupport.clearAllIndexesAndRegistries();
        try {
            Thread.sleep(5000); //Sleep for 5s to allow any Operating System clearing.
        } catch (InterruptedException ex) {
            LOGGER.error("Exception closing Jena: {}", ex.getMessage());
        }
        LOGGER.debug("GeosparqlJena closed");
    }

    @Override
    public String translateQuery(String query) {
        //No query translation required.
        return query;
    }

    private class QueryTask implements Runnable {

        private final String queryString;
        private final Dataset dataset;
        private QueryResult queryResult;

        public QueryTask(String queryString, Dataset dataset) {
            this.queryString = queryString;
            this.dataset = dataset;
            this.queryResult = new QueryResult();
        }

        public QueryResult getQueryResult() {
            return queryResult;
        }

        @Override
        public void run() {
            runQuery();
        }

        public void runQuery() {

            Boolean isComplete = true;
            List<List<VarValue>> results = new ArrayList<>();
            long startNanoTime = System.nanoTime();
            long queryNanoTime;
            dataset.begin(ReadWrite.READ);
            try (QueryExecution qexec = QueryExecutionFactory.create(queryString, dataset)) {
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
                            LOGGER.error("Anon Node result: {}", valueStr);
                        }
                        VarValue varValue = new VarValue(varName, valueStr);
                        result.add(varValue);
                    }
                    results.add(result);
                }
            } catch (Exception ex) {
                LOGGER.error("Exception: {}", ex.getMessage());
                queryNanoTime = startNanoTime;
                results.clear();
                isComplete = false;
            } finally {
                dataset.end();
            }

            long resultsNanoTime = System.nanoTime();
            this.queryResult = new QueryResult(startNanoTime, queryNanoTime, resultsNanoTime, results, isComplete);
            LOGGER.info("Query Evaluation Time - Start->Query: {}, Query->Results: {}, Start->Results: {}", queryResult.getStartQueryDuration(), queryResult.getQueryResultsDuration(), queryResult.getStartResultsDuration());
        }
    }

}
