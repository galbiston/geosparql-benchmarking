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
import static execution.BenchmarkExecution.RESULTS_FOLDER;
import execution_results.DatasetLoadResult;
import execution_results.DatasetLoadTimeResult;
import execution.TestSystem;
import execution.TestSystemFactory;
import static geosparql_benchmarking.parliament.Parliament_TestSystem.SPATIAL_INDEX_FACTORY;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gerg
 */
public class Parliament_TestSystemFactory implements TestSystemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String TEST_SYSTEM_NAME = "Parliament";
    private final File resultsFolder;
    private final File parliamentKnowledgeBaseFolder;

    public Parliament_TestSystemFactory(String resultsFolder, File parliamentKnowledgeBaseFolder) {
        this.resultsFolder = new File(RESULTS_FOLDER, resultsFolder);
        this.resultsFolder.mkdir();
        this.parliamentKnowledgeBaseFolder = parliamentKnowledgeBaseFolder;
    }

    @Override
    public TestSystem getTestSystem() {
        return new Parliament_TestSystem();
    }

    @Override
    public String getTestSystemName() {
        return TEST_SYSTEM_NAME;
    }

    @Override
    public File getResultsFolder() {
        return resultsFolder;
    }

    @Override
    public Boolean clearDataset() {
        return clearDataset(parliamentKnowledgeBaseFolder);
    }

    @Override
    public DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap, Integer iteration) {
        return loadDatasetStatic(datasetMap, iteration);
    }

    @Override
    public Boolean clearLoadDataset(TreeMap<String, File> datasetMap) {

        Boolean isClear = clearDataset();
        if (isClear) {
            DatasetLoadResult loadResult = loadDataset(datasetMap, 0);
            return loadResult.getIsCompleted();
        }
        return isClear;
    }

    public static Boolean clearDataset(File parliamentKnowledgeBaseFolder) {
        LOGGER.info("Parliament Knowledge Base is controlled in the ParliamentConfig.txt file.");
        try {
            FileUtils.deleteDirectory(parliamentKnowledgeBaseFolder);
            return true;
        } catch (IOException ex) {
            LOGGER.error("Parliament KB Folder deletion: {} - {}", parliamentKnowledgeBaseFolder.getAbsolutePath(), ex.getMessage());
            return false;
        }
    }

    /**
     * Loads the dataset into the target location. No clearing of the dataset is
     * done before this and may be recommended.
     *
     * @param datasetMap
     * @return
     */
    public static DatasetLoadResult loadDataset(TreeMap<String, File> datasetMap) {
        return loadDatasetStatic(datasetMap, 0);
    }

    private static DatasetLoadResult loadDatasetStatic(TreeMap<String, File> datasetMap, Integer iteration) {
        LOGGER.info("Parliament Loading: Started");
        LOGGER.info("Parliament inferencing is controlled in the ParliamentConfig.txt file.");
        List<DatasetLoadTimeResult> datasetLoadTimeResults = new ArrayList<>();
        Boolean isCompleted = true;
        long startNanoTime = System.nanoTime();

        IndexFactoryRegistry.getInstance().setIndexingEnabledByDefault(true);
        IndexManager indexManager = IndexManager.getInstance();

        //Parliament graph store that contains all graphs. Requires default graph for constructor.
        KbGraph defaultGraph = KbGraphFactory.createDefaultGraph();
        KbGraphStore graphStore = new KbGraphStore(defaultGraph);
        Model defaultModel = ModelFactory.createModelForGraph(defaultGraph);
        try {
            graphStore.initialize();
            //Register named graph and index with IndexManager.
            indexManager.createAndRegister(defaultGraph, KbGraphStore.DEFAULT_GRAPH_NODE, SPATIAL_INDEX_FACTORY);

            //Named graphs for each file loaded.
            for (Map.Entry<String, File> entry : datasetMap.entrySet()) {
                String graphName = entry.getKey();
                File sourceRDFFile = entry.getValue();

                long datasetStartNanoTime = System.nanoTime();
                if (graphName.isEmpty()) {
                    LOGGER.info("Loading - {} into default graph: Started", sourceRDFFile);
                    defaultModel.read(sourceRDFFile.toURI().toURL().toString(), "N-TRIPLE");
                    LOGGER.info("Loading - {} into default graph: Completed", sourceRDFFile);

                    //Register default graph and index with IndexManager.
                    LOGGER.info("Spatial Indexing - default graph : Started");

                    graphStore.setIndexingEnabled(KbGraphStore.DEFAULT_GRAPH_NODE, true);
                    indexManager.createAndRegister(defaultGraph, KbGraphStore.DEFAULT_GRAPH_NODE, SPATIAL_INDEX_FACTORY);
                    //Force the building of the index for the graph.
                    indexManager.rebuild(defaultGraph);
                    indexManager.flush(defaultGraph);
                    indexManager.closeAll(defaultGraph);
                    LOGGER.info("Spatial Indexing - default graph : Completed");
                } else {
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

                long datasetEndNanoTime = System.nanoTime();
                DatasetLoadTimeResult datasetLoadTimeResult = new DatasetLoadTimeResult(graphName, datasetStartNanoTime, datasetEndNanoTime);
                datasetLoadTimeResults.add(datasetLoadTimeResult);
            }
        } catch (MalformedURLException ex) {
            isCompleted = false;
            LOGGER.error("Exception: {}", ex.getMessage());
        } finally {
            graphStore.flush();
            graphStore.close();
        }
        long endNanoTime = System.nanoTime();
        LOGGER.info("Parliament Loading: Completed");
        return new DatasetLoadResult(TEST_SYSTEM_NAME, isCompleted, iteration, startNanoTime, endNanoTime, datasetLoadTimeResults);
    }

}
