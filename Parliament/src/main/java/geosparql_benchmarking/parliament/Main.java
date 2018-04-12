package geosparql_benchmarking.parliament;

import com.bbn.parliament.jena.graph.KbGraphFactory;
import com.bbn.parliament.jena.graph.KbGraphStore;
import com.hp.hpl.jena.graph.Node;
import geosparql_benchmarking.DatasetSources;
import geosparql_benchmarking.GraphURI;
import geosparql_benchmarking.experiments.BenchmarkExecution;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String PARLIAMENT_RESULTS_FOLDER_NAME = "parliament";
    public static final File PARLIAMENT_KNOWLEDGE_BASE_FOLDER = new File("parliament_kb");

    /**
     * @param args the command line arguments<br>
     * The Parliament DLL folder needs to added to the PATH and the
     * ParliamentConfig.txt filepath set to the PARLIAMENT_CONFIG_PATH
     * environment variables so that they are available on the java.library.path
     * property.
     */
    public static void main(String[] args) {

        HashMap<String, File> datasetMap = DatasetSources.getCRS84Datasets();
        //ParliamentTestSystemFactory.loadDataset(datasetMap);
        ParliamentTestSystemFactory testSystemFactory = new ParliamentTestSystemFactory(PARLIAMENT_RESULTS_FOLDER_NAME, PARLIAMENT_KNOWLEDGE_BASE_FOLDER);
        //runDatasetLoad(testSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //runParliament(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_MAP);
    }

    public static void runDatasetLoad(ParliamentTestSystemFactory testSystemFactory, Integer iterations, HashMap<String, File> datasetMap) {
        BenchmarkExecution.runDatasetLoad(testSystemFactory, iterations, datasetMap);
    }

    public static void runParliament(ParliamentTestSystemFactory testSystemFactory, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {

        BenchmarkExecution.runWarm(testSystemFactory, iterations, timeout, queryMap);
        BenchmarkExecution.runCold(testSystemFactory, iterations, timeout, queryMap);
    }

    private static void rdfsParliamentTest() {

        KbGraphStore graphStore = new KbGraphStore(KbGraphFactory.createDefaultGraph());
        graphStore.initialize();
        com.hp.hpl.jena.query.Dataset dataSource = com.hp.hpl.jena.query.DatasetFactory.create(graphStore);
        com.hp.hpl.jena.rdf.model.Model unionModel = com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(graphStore.getMasterGraph());
        String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://www.opengis.net/ont/sf#>";
        //String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        try {
            com.hp.hpl.jena.query.QueryExecution qe = com.hp.hpl.jena.query.QueryExecutionFactory.create(queryString, dataSource);
            com.hp.hpl.jena.query.ResultSet rs = qe.execSelect();
            com.hp.hpl.jena.query.ResultSetFormatter.outputAsCSV(rs);
            qe.close();
        } catch (Exception ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }
        graphStore.close();
    }

    private static void exportParliamentTest() {

        KbGraphStore graphStore = new KbGraphStore(KbGraphFactory.createDefaultGraph());
        graphStore.initialize();
        Node graphNode = Node.createURI(GraphURI.LGD_URI);
        com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(graphStore.getGraph(graphNode));

        try (FileOutputStream out = new FileOutputStream(new File("lgd-parliament.ttl"))) {
            model.write(out, "TTL");
        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }
        graphStore.close();
    }

}
