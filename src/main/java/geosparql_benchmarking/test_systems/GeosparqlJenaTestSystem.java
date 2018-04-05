/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.test_systems;

import geosparql_benchmarking.experiments.BenchmarkExecution;
import geosparql_benchmarking.experiments.QueryResult;
import geosparql_benchmarking.experiments.TestSystem;
import implementation.GeoSPARQLModel;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeosparqlJenaTestSystem implements TestSystem {

    final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Dataset dataset = null;
    private final File datasetFolder;

    public GeosparqlJenaTestSystem(File datasetFolder) {
        this.datasetFolder = datasetFolder;
        this.dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
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
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        } catch (TimeoutException ex) {
            LOGGER.info("GeoSPARQL Jena Query Timeout: Restarting");
            this.restart();
        } finally {
            LOGGER.debug("GeoSPARQL Jena: Executor Shutdown");
            executor.shutdown();
            System.gc();
        }

        QueryResult queryResult = runnable.getQueryResult();
        LOGGER.debug("GeoSPARQL Jena Query: {}", queryResult);

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

        return new QueryResult(startNanoTime, resultsNanoTime, true);
    }

    @Override
    public String getName() {
        return "GeoSparqlJena";
    }

    @Override
    public void initialize() {
        GeoSPARQLModel.loadFunctions();
    }

    @Override
    public void close() {
        TDBFactory.release(dataset);
        dataset = null;
    }

    @Override
    public void clearCaches() {
        //Release resources and reconnect.
        if (dataset != null) {
            TDBFactory.release(dataset);
        }
        this.dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
    }

    @Override
    public void restart() {
        //No restarting but release resources and reconnect.
        TDBFactory.release(dataset);
        this.dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
    }

    @Override
    public String translateQuery(String query, String label) {
        //No query translation.
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

            LOGGER.info("Evaluating query");
            List<HashMap<String, String>> results = new ArrayList<>();
            boolean isCompleted = true;
            long startNanoTime = System.nanoTime();
            long queryNanoTime;
            dataset.begin(ReadWrite.READ);
            try (QueryExecution qexec = QueryExecutionFactory.create(queryString, dataset)) {
                ResultSet rs = qexec.execSelect();
                queryNanoTime = System.nanoTime();
                while (rs.hasNext()) {
                    QuerySolution querySolution = rs.next();
                    Iterator<String> varNames = querySolution.varNames();
                    HashMap<String, String> result = new HashMap<>();

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
                        result.put(varName, valueStr);

                    }
                    results.add(result);
                }

            } catch (Exception ex) {
                LOGGER.error("Execption: {}", ex.getMessage());
                queryNanoTime = startNanoTime;
                isCompleted = false;
            } finally {
                dataset.end();
            }

            long resultsNanoTime = System.nanoTime();
            LOGGER.info("Elapsed Time - Query: {}, Results: {}", queryNanoTime - startNanoTime, resultsNanoTime - queryNanoTime);
            this.queryResult = new QueryResult(startNanoTime, queryNanoTime, resultsNanoTime, results, isCompleted);
        }
    }

    public static void loadDataset(File datasetFolder, HashMap<String, File> datasetMap, Boolean inferenceEnabled) {
        LOGGER.info("Geosparql Jena Loading: Started");
        Dataset dataset = TDBFactory.createDataset(datasetFolder.getAbsolutePath());
        Model geosparqlSchema = RDFDataMgr.loadModel(BenchmarkExecution.class.getClassLoader().getResource("geosparql_vocab_all.rdf").toString());

        for (Map.Entry<String, File> entry : datasetMap.entrySet()) {
            try {
                dataset.begin(ReadWrite.WRITE);
                String sourceRDFFile = entry.getValue().getAbsolutePath();
                String graph = entry.getKey();
                LOGGER.info("Loading: {} into {}: Started", sourceRDFFile, graph);
                Model dataModel = RDFDataMgr.loadModel(sourceRDFFile);
                if (inferenceEnabled) {
                    InfModel infModel = ModelFactory.createRDFSModel(geosparqlSchema, dataModel);
                    infModel.prepare();
                    dataset.addNamedModel(graph, infModel);
                } else {
                    dataset.addNamedModel(graph, dataModel);
                }
                LOGGER.info("Loading: {} into {}: Completed", sourceRDFFile, graph);
                dataset.commit();

            } catch (RuntimeException ex) {
                LOGGER.error("TDB Load Error: {}", ex.getMessage());
            } finally {
                dataset.end();
            }
        }
        dataset.close();
        TDBFactory.release(dataset);

        LOGGER.info("Geosparql Jena Loading: Completed");
    }

}
