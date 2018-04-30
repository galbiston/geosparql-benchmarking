/**
 */
package geosparql_benchmarking.parliament;

import com.bbn.parliament.jena.graph.KbGraph;
import com.bbn.parliament.jena.graph.KbGraphFactory;
import com.bbn.parliament.jena.graph.KbGraphStore;
import com.bbn.parliament.jena.graph.index.IndexFactoryRegistry;
import com.bbn.parliament.jena.graph.index.IndexManager;
import com.bbn.parliament.jena.graph.index.spatial.Constants;
import com.bbn.parliament.jena.graph.index.spatial.SpatialIndexFactory;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
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
import geosparql_benchmarking.experiments.VarValue;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
 * Parliament spatial indexing and queries only works with "jena-arq:2.9.4"
 * dependency. This means that Parliament and GeoSPARQL Jena cannot be run in
 * the same project. This will be resolved if Parliament is kept up to date with
 * the the Jena API.
 */
public class ParliamentTestSystem implements TestSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //Registering factory makes existing indexes available, otherwise they are ignored.
    public static final SpatialIndexFactory SPATIAL_INDEX_FACTORY = createSpatialIndexFactory();

    KbGraphStore graphStore;
    Dataset dataset;

    public ParliamentTestSystem() {
        LOGGER.info("Parliament Initialisation: Started");

        //Set Factory Registry to index by default.
        IndexFactoryRegistry.getInstance().setIndexingEnabledByDefault(true);

        //Parliament graph store that contains all graphs. Requires default graph for constructor.
        KbGraph graph = KbGraphFactory.createDefaultGraph();
        graphStore = new KbGraphStore(graph);
        graphStore.initialize();

        //Dataset used for queries.
        dataset = graphStore.toDataset();

        LOGGER.info("Parliament Initialisation: Completed");
    }

    private void checkGraphIndexes() {
        LOGGER.debug("Index Default Enabled: {}", IndexFactoryRegistry.getInstance().isIndexingEnabledByDefault());
        IndexManager indexManager = IndexManager.getInstance();
        Iterator<Node> graphNodes = graphStore.listGraphNodes();
        while (graphNodes.hasNext()) {
            Node graphNode = graphNodes.next();
            Graph namedGraph = graphStore.getGraph(graphNode);
            Boolean isIndexEnabled = isIndexingEnabled(graphNode, graphStore);
            Boolean hasIndex = indexManager.hasIndexes(namedGraph);
            LOGGER.debug("Graph: {}, Index Enabled: {}, Has Index: {}", graphNode, isIndexEnabled, hasIndex);
        }
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
        QueryTask runnable = new QueryTask(query, dataset);
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

        if (dataset != null) {
            dataset.close();
        }

        if (graphStore != null) {
            graphStore.close();
        }
        System.gc();
        try {
            Thread.sleep(5000); //Sleep for 5s to allow any Operating System clearing.
        } catch (InterruptedException ex) {
            LOGGER.error("Exception closing Parliament: {}", ex.getMessage());
        }
        LOGGER.debug("Parliament closed");

    }

    @Override
    public String translateQuery(String query) {
        //Query translation shold not be required as Parliament has aligned with GeoSPARQL 1.0 namespaces.
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
                LOGGER.error("Exception: {}", ex.getMessage());
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

    private static SpatialIndexFactory createSpatialIndexFactory() {
        //Create spatial index factory and configure for GeoSPARQL.
        SpatialIndexFactory factory = new SpatialIndexFactory();
        Properties properties = new Properties();
        properties.setProperty(Constants.GEOMETRY_INDEX_TYPE, Constants.GEOMETRY_INDEX_RTREE);
        properties.setProperty(Constants.GEOSPARQL_ENABLED, Boolean.TRUE.toString());
        factory.configure(properties);

        //Register spatial index factory. Enable indexing for all graphs. Enabling individual graphs in the graph store does not work.
        IndexFactoryRegistry.getInstance().register(factory);
        return factory;
    }

}
