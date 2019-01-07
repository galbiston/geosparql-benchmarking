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
package io.github.galbiston.geosparql_benchmarking.parliament;

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
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import io.github.galbiston.execution.QueryTask;
import io.github.galbiston.execution.TestSystem;
import io.github.galbiston.execution_results.QueryResult;
import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parliament spatial indexing and queries only works with "jena-arq:2.9.4"
 * dependency. This means that Parliament and GeoSPARQL Jena cannot be run in
 * the same project. This will be resolved if Parliament is kept up to date with
 * the the Jena API.
 */
public class Parliament_TestSystem implements TestSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //Registering factory makes existing indexes available, otherwise they are ignored.
    public static final SpatialIndexFactory SPATIAL_INDEX_FACTORY = createSpatialIndexFactory();

    KbGraphStore graphStore;
    Dataset dataset;

    public Parliament_TestSystem() {
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
    public QueryTask getQueryTask(String query) {
        return new Parliament_QueryTask(query, dataset);
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
        try {
            System.gc();
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
