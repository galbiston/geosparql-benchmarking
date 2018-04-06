/**
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
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
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
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import geosparql_benchmarking.experiments.QueryResult;
import geosparql_benchmarking.experiments.TestSystem;
import java.io.File;
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
 *
 */
public class ParliamentTestSystem implements TestSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    Dataset dataSource = null;
    KbGraphStore graphStore = null;

    public ParliamentTestSystem() {
    }

    @Override
    public String getName() {
        return "Parliament";
    }

    /**
     * The spatial indexing only works with "jena-arq:2.9.4" depenendency and
     * GeoSPARQL Jena is excluded from the project. This is due to package name
     * issues in "jena-arq:2.9.4" and Parliament not being updated with the Jena
     * API.
     */
    @Override
    public void initialize() {
        LOGGER.info("Parliament Initialisation: Started");

        //Parliament graph store that contains all graphs. Requires default graph for constructor.
        KbGraph graph = KbGraphFactory.createDefaultGraph();
        graphStore = new KbGraphStore(graph);
        graphStore.initialize();

        //Datasource tused for queries.
        dataSource = DatasetFactory.create(graphStore);

        LOGGER.info("Parliament Initialisation: Completed");
    }

    private boolean isIndexingEnabled(Node graphName, KbGraphStore graphStore) {

        Graph masterGraph = graphStore.getMasterGraph();
        ExtendedIterator<Triple> it = masterGraph.find(graphName, RDF.Nodes.type, Node.createURI(KbGraphStore.INDEXED_GRAPH));
        try {
            return it.hasNext();
        } finally {
            if (null != it) {
                it.close();
            }
        }
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
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
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

        return new QueryResult(startNanoTime, resultsNanoTime);
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
    public String translateQuery(String query) {
        //Query translation shold not be required as Parliament has aligned with GeoSPARQL 1.0 namespaces.
        return query;
    }

    private class QueryTask implements Runnable {

        private final String queryString;
        private final Dataset dataSource;
        private QueryResult queryResult;

        public QueryTask(String queryString, Dataset dataSource) {
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
                results.clear();
            } finally {
                if (qexec != null) {
                    qexec.close();
                }
            }
            long resultsNanoTime = System.nanoTime();
            this.queryResult = new QueryResult(startNanoTime, queryNanoTime, resultsNanoTime, results);
            LOGGER.info("Query Evaluation Time - Start->Query: {}, Query->Results: {}, Start->Results: {}", queryResult.getStartQueryDuration(), queryResult.getQueryResultsDuration(), queryResult.getStartResultsDuration());
        }
    }

    public static void loadDataset(HashMap<String, File> datasetMap) {
        LOGGER.info("Parliament Loading: Started");
        LOGGER.info("Parliament inferencing is controlled in the ParliamentConfig.txt file.");

        //Create spatial index factory and configure for GeoSPARQL.
        SpatialIndexFactory factory = new SpatialIndexFactory();
        Properties properties = new Properties();
        properties.setProperty(Constants.GEOMETRY_INDEX_TYPE, Constants.GEOMETRY_INDEX_RTREE);
        properties.setProperty(Constants.GEOSPARQL_ENABLED, Boolean.TRUE.toString());
        factory.configure(properties);

        //Register spatial index factory. Enable indexing for all graphs. Enabling individual graphs in the graph store does not work.
        IndexFactoryRegistry.getInstance().register(factory);
        IndexFactoryRegistry.getInstance().setIndexingEnabledByDefault(true);

        //Parliament graph store that contains all graphs. Requires default graph for constructor.
        KbGraph graph = KbGraphFactory.createDefaultGraph();
        KbGraphStore graphStore = new KbGraphStore(graph);
        try {
            graphStore.initialize();
            //Create spatial index from factory
            SpatialIndex index = factory.createIndex(graph, null);
            //Register named graph and index with IndexManager.
            IndexManager.getInstance().register(graph, null, factory, index);

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

                index = factory.createIndex(namedGraph, graphNode);
                //Register named graph and index with IndexManager.
                IndexManager.getInstance().register(namedGraph, graphNode, factory, index);

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
