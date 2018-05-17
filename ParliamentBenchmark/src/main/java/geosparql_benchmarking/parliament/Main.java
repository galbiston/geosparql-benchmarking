package geosparql_benchmarking.parliament;

import com.bbn.parliament.jena.graph.KbGraphFactory;
import com.bbn.parliament.jena.graph.KbGraphStore;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import data_setup.BenchmarkParameters;
import data_setup.GraphURI;
import execution.BenchmarkExecution;
import execution.ExecutionParameters;
import execution.QueryCase;
import execution.TestSystem;
import execution_results.QueryResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
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

        try {
            ExecutionParameters parameters = ExecutionParameters.extract(args);

            ParliamentTestSystemFactory testSystemFactory = new ParliamentTestSystemFactory(PARLIAMENT_RESULTS_FOLDER_NAME, PARLIAMENT_KNOWLEDGE_BASE_FOLDER);
            BenchmarkExecution.runType(testSystemFactory, parameters);
        } catch (Exception ex) {
            LOGGER.error("{} for arguments {}", ex.getMessage(), args);
        }
        /*

        TreeMap<String, File> datasetMap = Dataset_CRS84.getAll();

        //Parliament
        ParliamentTestSystemFactory testSystemFactory = new ParliamentTestSystemFactory(PARLIAMENT_RESULTS_FOLDER_NAME, PARLIAMENT_KNOWLEDGE_BASE_FOLDER);
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
        //BenchmarkExecution.runWarm(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);

        List<QueryCase> queries = MicroBenchmark.loadMainQuerySet();
        //BenchmarkExecution.runWarm(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, queries.subList(0, 17), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
        //BenchmarkExecution.runBoth(testSystemFactory, 1, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadNonTopologicalFunctionsQuery_3(), BenchmarkParameters.RESULT_LINE_LIMIT_5000);
        //rdfsParliamentTest();
        //bufferQueryTest();
        //equalsTest();
        //equalsTest2();
        //Data Loading
        //ParliamentTestSystemFactory.clearDataset(PARLIAMENT_KNOWLEDGE_BASE_FOLDER);
        //ParliamentTestSystemFactory.loadDataset(datasetMap);
        //Repeated Data Loading
        //runDatasetLoad(testSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
         */
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

    private static void equalsTest() {

        ParliamentTestSystemFactory testSystemFactory = new ParliamentTestSystemFactory(PARLIAMENT_RESULTS_FOLDER_NAME, PARLIAMENT_KNOWLEDGE_BASE_FOLDER);

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POINT (23.71 37.98)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
                + "BIND(\"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(37.98 23.71)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?second)"
                + "BIND(geof:sfEquals(?first, ?second) AS ?res) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = testSystem.runQueryWithTimeout(queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest2() {

        ParliamentTestSystemFactory testSystemFactory = new ParliamentTestSystemFactory(PARLIAMENT_RESULTS_FOLDER_NAME, PARLIAMENT_KNOWLEDGE_BASE_FOLDER);

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> LINESTRING(0 0, 2 0, 5 0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> LINESTRING(5 0, 0 0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?second)"
                + "BIND(geof:sfEquals(?first, ?second) AS ?res) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = testSystem.runQueryWithTimeout(queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

}
