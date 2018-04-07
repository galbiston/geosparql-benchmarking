/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.parliament;

import com.bbn.parliament.jena.graph.KbGraph;
import com.bbn.parliament.jena.graph.KbGraphFactory;
import com.bbn.parliament.jena.graph.KbGraphStore;
import com.bbn.parliament.jena.graph.index.IndexFactoryRegistry;
import com.bbn.parliament.jena.graph.index.IndexManager;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import static geosparql_benchmarking.experiments.BenchmarkExecution.RESULTS_FOLDER;
import geosparql_benchmarking.experiments.TestSystem;
import geosparql_benchmarking.experiments.TestSystemFactory;
import static geosparql_benchmarking.parliament.ParliamentTestSystem.SPATIAL_INDEX_FACTORY;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class ParliamentTestSystemFactory implements TestSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final File resultsFolder;

    public ParliamentTestSystemFactory(String resultsFolder) {
        this.resultsFolder = new File(RESULTS_FOLDER, resultsFolder);
        this.resultsFolder.mkdir();
    }

    @Override
    public TestSystem getTestSystem() {
        return new ParliamentTestSystem();
    }

    @Override
    public String getTestSystemName() {
        return "Parliament";
    }

    @Override
    public File getResultsFolder() {
        return resultsFolder;
    }

    public static void loadDataset(HashMap<String, File> datasetMap) {
        LOGGER.info("Parliament Loading: Started");
        LOGGER.info("Parliament inferencing is controlled in the ParliamentConfig.txt file.");

        IndexFactoryRegistry.getInstance().setIndexingEnabledByDefault(true);
        IndexManager indexManager = IndexManager.getInstance();

        //Parliament graph store that contains all graphs. Requires default graph for constructor.
        KbGraph graph = KbGraphFactory.createDefaultGraph();
        KbGraphStore graphStore = new KbGraphStore(graph);
        try {
            graphStore.initialize();
            //Register named graph and index with IndexManager.
            indexManager.createAndRegister(graph, KbGraphStore.DEFAULT_GRAPH_NODE, SPATIAL_INDEX_FACTORY);

            //Named graphs for each file loaded.
            for (Map.Entry<String, File> entry : datasetMap.entrySet()) {
                String graphName = entry.getKey();
                File sourceRDFFile = entry.getValue();

                //Data loading into the named graph.
                LOGGER.info("Loading - {} into {}: Started", sourceRDFFile, graphName);
                Node graphNode = Node.createURI(graphName);
                KbGraph namedGraph = KbGraphFactory.createNamedGraph();
                Model namedModel = ModelFactory.createModelForGraph(namedGraph);
                namedModel.read(sourceRDFFile.toURI().toURL().toString(), "N-TRIPLE");
                graphStore.addGraph(graphNode, namedGraph);
                LOGGER.info("Loading - {} into {}: Completed", sourceRDFFile, graphName);

                //Register named graph and index with IndexManager.
                LOGGER.info("Spatial Indexing - {} : Started", graphName);
                graphStore.setIndexingEnabled(graphNode, true);
                indexManager.createAndRegister(namedGraph, graphNode, SPATIAL_INDEX_FACTORY);
                //Force the building of the index for the graph.
                indexManager.rebuild(namedGraph);
                indexManager.flush(namedGraph);
                indexManager.closeAll(namedGraph);
                LOGGER.info("Spatial Indexing - {} : Completed", graphName);
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
