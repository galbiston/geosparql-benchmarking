package gr.uoa.di.rdf.Geographica.parliament;

import com.bbn.parliament.jena.graph.KbGraph;
import com.bbn.parliament.jena.graph.KbGraphFactory;
import com.bbn.parliament.jena.graph.KbGraphStore;
import com.bbn.parliament.jena.graph.index.IndexFactoryRegistry;
import com.bbn.parliament.jena.graph.index.spatial.Constants;
import com.bbn.parliament.jena.graph.index.spatial.SpatialIndexFactory;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import gr.uoa.di.rdf.Geographica.systemsundertest.RunSystemUnderTest;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunParliament extends RunSystemUnderTest {

    final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    // @Override
    // protected void addOptions() {
    // super.addOptions();
    // }
    // @Override
    // protected void logOptions() {
    // super.logOptions();
    // }
    @Override
    protected void initSystemUnderTest() throws Exception {
        sut = new ParliamentSUT();
    }

    public static void main(String[] args) throws Exception {
        RunSystemUnderTest runParliament = new RunParliament();

        runParliament.run(args);
    }

    public static void loadDataset(HashMap<String, File> datasetMap) throws MalformedURLException {
        LOGGER.info("Parliament Loading: Started");
        LOGGER.info("Parliament inferencing is controlled in the ParliamentConfig.txt file.");
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
            for (Entry<String, File> entry : datasetMap.entrySet()) {
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
        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        } finally {
            graphStore.flush();
            graphStore.close();
        }
        LOGGER.info("Parliament Loading: Completed");
    }

    public static void runBenchmark(File resultsFolder, Integer runtime, Integer timeout, List<String> queryList) {

        for (String query : queryList) {
            try {
                LOGGER.info("Parliament Benchmark - {}: Started", query);
                String[] experimentArgs = {"--logpath", resultsFolder.getAbsolutePath(), "--runtime", runtime.toString(), "--timeout", timeout.toString(), "run", query};
                RunParliament.main(experimentArgs);
                LOGGER.info("Parliament Benchmark - {}: Completed", query);
            } catch (Exception ex) {
                LOGGER.error("Exception: {}", ex);
            }
        }
    }

}
