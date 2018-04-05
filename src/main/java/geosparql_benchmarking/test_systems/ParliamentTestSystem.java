/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright (C) 2013, Pyravlos Team
 *
 */
package geosparql_benchmarking.test_systems;

import com.bbn.parliament.jena.graph.KbGraph;
import com.bbn.parliament.jena.graph.KbGraphFactory;
import com.bbn.parliament.jena.graph.KbGraphStore;
import com.bbn.parliament.jena.graph.index.IndexFactoryRegistry;
import com.bbn.parliament.jena.graph.index.IndexManager;
import com.bbn.parliament.jena.graph.index.spatial.Constants;
import com.bbn.parliament.jena.graph.index.spatial.SpatialIndex;
import com.bbn.parliament.jena.graph.index.spatial.SpatialIndexFactory;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import geosparql_benchmarking.experiments.QueryResult;
import geosparql_benchmarking.experiments.TestSystem;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author George Garbis <ggarbis@di.uoa.gr>
 * @author Kostis Kyzirakos <kkyzir@di.uoa.gr>
 *
 */
public class ParliamentTestSystem implements TestSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    DataSource dataSource = null;
    KbGraphStore graphStore = null;

    public ParliamentTestSystem() {
    }

    @Override
    public String getName() {
        return "Parliament";
    }

    /**
     * This is based on the Geographica code for Parliament initialising
     * (reduced down the number of redundant data members). It would seem to be
     * problematic as the spatial indexing is not used (or working).
     */
    @Override
    public void initialize() {
        if (!java.lang.System.getProperty("java.library.path").contains("parliament-dependencies")) {
            LOGGER.warn("Do not forget to add the folder Geographica/runtime/src/main/resources/parliament-dependencies/linux-32 or 64 to the variable java.library.path.");
            LOGGER.warn("You will possible get some exceptions...");
        }

        LOGGER.info("Initializing Parliament...");
        // create spatial index factory and configure for GeoSPARQL. This is used
        // by the GraphStore whenever a new named graph is created.
        SpatialIndexFactory factory = new SpatialIndexFactory();
        Properties properties = new Properties();
        properties.setProperty(Constants.GEOMETRY_INDEX_TYPE, Constants.GEOMETRY_INDEX_RTREE);
        properties.setProperty(Constants.GEOSPARQL_ENABLED, Boolean.TRUE.toString());
        factory.configure(properties);

        // register factory
        IndexFactoryRegistry.getInstance().register(factory);

        // create a Parliament graph and graph store
        KbGraph graph = KbGraphFactory.createDefaultGraph();
        graphStore = new KbGraphStore(graph);
        graphStore.initialize();

        // DO NOT RE-INDEX THE GRAPH!!!!!
        boolean firstTime = false;
        if (firstTime) {
            // create spatial index from factory
            SpatialIndex index = factory.createIndex(graph, null);

            // register index with IndexManager
            IndexManager.getInstance().register(graph, null, factory, index);

            // the following tells the graph store that the graph is now an
            // indexing graph. This is necessary so that the next time Parliament
            // loads, the index is read in automatically.
            graphStore.setIndexingEnabled(KbGraphStore.DEFAULT_GRAPH_NODE, true);
        }

        // create a new datasource (that will be used later on to append named graphs)
        dataSource = DatasetFactory.create(graphStore);

        LOGGER.info("Parliament initialized");
    }

    @Override
    public QueryResult runQueryWithTimeout(String query, Duration timeout) {

        ExecutorService executor = Executors.newFixedThreadPool(1);
        QueryTask runnable = new QueryTask(query, dataSource);
        Future<?> future = executor.submit(runnable);

        try {
            LOGGER.debug("Parliament Future: Started");
            future.get(timeout.getSeconds(), TimeUnit.SECONDS);
            LOGGER.debug("Parliament Future: Completed");
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        } catch (TimeoutException ex) {
            LOGGER.info("Parliament Query Timeout: Restarting");
            this.restart();
        } finally {
            LOGGER.debug("Parliament: Executor shutdown");
            executor.shutdown();
            System.gc();
        }

        QueryResult queryResult = runnable.getQueryResult();
        LOGGER.debug("Parliament Query: {}", queryResult);

        return queryResult;
    }

    @Override
    public QueryResult runUpdate(String update) {

        LOGGER.info("Parliament Update: Started");
        long startNanoTime = System.nanoTime();
        UpdateRequest u = UpdateFactory.create(update, Syntax.syntaxARQ);
        UpdateProcessor updateProcessor = UpdateExecutionFactory.create(u, graphStore);
        updateProcessor.execute();
        long resultsNanoTime = System.nanoTime();
        LOGGER.info("Parliament Update: Completed");

        return new QueryResult(startNanoTime, resultsNanoTime, true);
    }

    @Override
    public void close() {

        if (dataSource != null) {
            dataSource.close();
        }

        if (graphStore != null) {
            graphStore.close();
        }
        System.gc();
        LOGGER.info("Parliament closed");

    }

    @Override
    public void restart() {
        try {
            LOGGER.info("No need to restart parliament");
//			LOGGER.info("Restarting Parliament...");

//			pr = Runtime.getRuntime().exec(restart_parliament);
//			pr.waitFor();
//			if ( pr.exitValue() != 0) {
//				LOGGER.error("Something went wrong while restarting Parliament");
//			}
//
//			Thread.sleep(5000);
//			LOGGER.info("Parliament restarted");
            //initialize(); // Parliament will be initializd out of clearCaches()
        } catch (Exception e) {
            LOGGER.error("Cannont clear caches");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            LOGGER.error(stacktrace);
        }
    }

    @Override
    public void clearCaches() {
        String[] clear_caches = {"/bin/sh", "-c", "sync && echo 3 > /proc/sys/vm/drop_caches"};
        Process pr;

        try {
            LOGGER.info("Clearing caches...");
            //close(); // clearCaches should be called after Parliament is closed

            pr = Runtime.getRuntime().exec(clear_caches);
            pr.waitFor();
            if (pr.exitValue() != 0) {
                LOGGER.error("Something went wrong while clearing caches");
            }

            Thread.sleep(5000);
            LOGGER.info("Caches cleared");

            //initialize(); // Parliament will be initializd out of clearCaches()
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Cannont clear caches");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            LOGGER.error(stacktrace);
        }
    }

    @Override
    public String translateQuery(String query) {
        String translatedQuery = query;
        /*
        //These should no longer be required as Parliament has aligned with GeoSPARQL 1.0 namespaces.
        translatedQuery = translatedQuery.replace("PREFIX geo: <http://www.opengis.net/ont/geosparql#>", "PREFIX geo: <http://www.opengis.net/ont/sf#>");
        translatedQuery = translatedQuery.replace("http://www.opengis.net/ont/geosparql#wktLiteral", "http://www.opengis.net/ont/sf#wktLiteral");
        translatedQuery = translatedQuery.replace("geo:wktLiteral", "<http://www.opengis.net/ont/sf#wktLiteral>");
         */
        return translatedQuery;
    }

    private class QueryTask implements Runnable {

        private final String queryString;
        private final DataSource dataSource;
        private QueryResult queryResult;

        public QueryTask(String queryString, DataSource dataSource) {
            this.queryString = queryString;
            this.dataSource = dataSource;
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

            LOGGER.info("Query Evaluation: Started");
            List<HashMap<String, String>> results = new ArrayList<>();
            boolean isCompleted = true;
            long startNanoTime = System.nanoTime();
            long queryNanoTime;
            QueryExecution qexec = null;
            try {
                qexec = QueryExecutionFactory.create(queryString, dataSource);
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
                            LOGGER.error("Anon Node result: " + valueStr);
                        }
                        result.put(varName, valueStr);
                    }
                    results.add(result);
                }
            } catch (Exception ex) {
                LOGGER.error("Exception: {}", ex.getMessage());
                queryNanoTime = startNanoTime;
                isCompleted = false;
            } finally {
                if (qexec != null) {
                    qexec.close();
                }
            }
            long resultsNanoTime = System.nanoTime();
            this.queryResult = new QueryResult(startNanoTime, queryNanoTime, resultsNanoTime, results, isCompleted);
            LOGGER.info("Query Evaluation Time - Start->Query: {}, Query->Results: {}, Start->Results: {}", queryResult.getStartQueryDuration(), queryResult.getQueryResultsDuration(), queryResult.getStartResultsDuration());
        }
    }

    public static void loadDataset(HashMap<String, File> datasetMap) {
        LOGGER.info("Parliament Loading: Started");
        LOGGER.info("Parliament inferencing is controlled in the ParliamentConfig.txt file.");

        // create spatial index factory and configure for GeoSPARQL. This is used
        // by the GraphStore whenever a new named graph is created.
        SpatialIndexFactory factory = new SpatialIndexFactory();
        Properties properties = new Properties();
        properties.setProperty(Constants.GEOMETRY_INDEX_TYPE, Constants.GEOMETRY_INDEX_RTREE);
        properties.setProperty(Constants.GEOSPARQL_ENABLED, Boolean.TRUE.toString());
        factory.configure(properties);

        // register factory
        IndexFactoryRegistry.getInstance().register(factory);

        // create a Parliament graph and graph store - default empty
        KbGraph graph = KbGraphFactory.createDefaultGraph();
        KbGraphStore graphStore = new KbGraphStore(graph);
        try {
            graphStore.initialize();
            // create spatial index from factory
            /*
        SpatialIndex index = factory.createIndex(graph, null);
        // register index with IndexManager
        IndexManager.getInstance().register(graph, null, factory, index);
        graphStore.setIndexingEnabled(KbGraphStore.DEFAULT_GRAPH_NODE, true);
             */
            //Named graphs for each file loaded.
            for (Map.Entry<String, File> entry : datasetMap.entrySet()) {
                String graphName = entry.getKey();
                File sourceRDFFile = entry.getValue();
                LOGGER.info("Loading: {} into {}: Started", sourceRDFFile, graphName);
                Node graphNode = Node.createURI(graphName);
                KbGraph namedGraph = KbGraphFactory.createNamedGraph();
                Model namedModel = ModelFactory.createModelForGraph(namedGraph);
                namedModel.read(sourceRDFFile.toURI().toURL().toString(), "N-TRIPLE");
                graphStore.addGraph(graphNode, namedGraph);
                /*
            index = factory.createIndex(namedGraph, graphNode);

            // register index with IndexManager
            IndexManager.getInstance().register(namedGraph, graphNode, factory, index);

            // the following tells the graph store that the graph is now an
            // indexing graph. This is necessary so that the next time Parliament
            // loads, the index is read in automatically.
            graphStore.setIndexingEnabled(graphNode, true);
                 */
                LOGGER.info("Loading: {} into {}: Completed", sourceRDFFile, graphName);
            }
        } catch (MalformedURLException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        } finally {
            graphStore.flush();
            graphStore.close();
        }
        LOGGER.info("Parliament Loading: Completed");
    }

}
