package geosparql_benchmarking.parliament;

import com.bbn.parliament.jena.graph.KbGraphFactory;
import com.bbn.parliament.jena.graph.KbGraphStore;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import geosparql_benchmarking.BenchmarkParameters;
import geosparql_benchmarking.DatasetSources;
import geosparql_benchmarking.GraphURI;
import geosparql_benchmarking.experiments.BenchmarkExecution;
import geosparql_benchmarking.experiments.QueryCase;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
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

        TreeMap<String, File> datasetMap = DatasetSources.getCRS84Datasets();

        ParliamentTestSystemFactory testSystemFactory = new ParliamentTestSystemFactory(PARLIAMENT_RESULTS_FOLDER_NAME, PARLIAMENT_KNOWLEDGE_BASE_FOLDER);
        //runDatasetLoad(testSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);

        //Parliament
        //BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_CASES, BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
        //BenchmarkExecution.runBoth(testSystemFactory, 1, BenchmarkParameters.TIMEOUT, QueryLoader.loadNonTopologicalFunctionsQuery_3(), BenchmarkParameters.RESULT_LINE_LIMIT_5000);
        rdfsParliamentTest();
        //bufferQueryTest();

        //Data Loading
        //ParliamentTestSystemFactory.clearDataset(PARLIAMENT_KNOWLEDGE_BASE_FOLDER);
        //ParliamentTestSystemFactory.loadDataset(datasetMap);
    }

    public static void runDatasetLoad(ParliamentTestSystemFactory testSystemFactory, Integer iterations, TreeMap<String, File> datasetMap) {
        BenchmarkExecution.runDatasetLoad(testSystemFactory, iterations, datasetMap);
    }

    private static void rdfsParliamentTest() {

        KbGraphStore graphStore = new KbGraphStore(KbGraphFactory.createDefaultGraph());
        graphStore.initialize();
        Dataset dataSource = com.hp.hpl.jena.query.DatasetFactory.create(graphStore);
        //Model unionModel = com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(graphStore.getMasterGraph());
        String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://www.opengis.net/ont/sf#>";
        //String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        try {
            QueryExecution qe = com.hp.hpl.jena.query.QueryExecutionFactory.create(queryString, dataSource);
            ResultSet rs = qe.execSelect();
            ResultSetFormatter.outputAsCSV(rs);
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

    private static void bufferQueryTest() {

        List<QueryCase> queryCases = new ArrayList<>();
        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
                + "PREFIX dataset: <http://geographica.di.uoa.gr/dataset/>\n"
                + "PREFIX geonames: <http://www.geonames.org/ontology#>\n"
                + "PREFIX opengis: <http://www.opengis.net/def/uom/OGC/1.0/>\n"
                + "\n"
                + "SELECT(geof:buffer(?o1, 0.04, opengis:radian) AS ?ret)\n"
                + "WHERE {\n"
                + "    GRAPH dataset:geonames {?s1 geonames:asWKT ?o1}\n"
                + "}";

        queryCases.add(new QueryCase("BufferQueryTest", "TestQuery", queryString));
        ParliamentTestSystemFactory testSystemFactory = new ParliamentTestSystemFactory(PARLIAMENT_RESULTS_FOLDER_NAME, PARLIAMENT_KNOWLEDGE_BASE_FOLDER);
        BenchmarkExecution.runCold(testSystemFactory, 1, BenchmarkParameters.TIMEOUT, queryCases, BenchmarkParameters.RESULT_LINE_LIMIT_5000);

    }

}
