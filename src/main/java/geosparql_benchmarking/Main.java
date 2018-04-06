package geosparql_benchmarking;

import geosparql_benchmarking.experiments.BenchmarkExecution;
import geosparql_benchmarking.experiments.BenchmarkExecution.TestSystemIdentifier;
import geosparql_benchmarking.experiments.QueryLoader;
import geosparql_benchmarking.test_systems.GeosparqlJenaTestSystem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        HashMap<String, File> datasetMap = getDatasets();
        Boolean inferenceEnabled = true;
        GeosparqlJenaTestSystem.loadDataset(GEOSPARQL_JENA_TDB_FOLDER, datasetMap, inferenceEnabled);
        //StrabonTestSystem.loadDataset(datasetMap, inferenceEnabled);

        //Benchmark
        BenchmarkExecution.createResultsFolders();
        Duration runtime = Duration.ofMinutes(30);
        Integer iterations = 1; //10;
        Duration timeout = Duration.ofSeconds(3600);

        HashMap<String, String> queryMap = QueryLoader.loadNonTopologicalFunctionsQueries();
        //HashMap<BenchmarkExecution.TestSystemIdentifier, File> testSystemFolders = BenchmarkExecution.getTestSystemFolders();
        //BenchmarkExecution.runAll(testSystemFolders, iterations, timeout, queryMap);
        runGeoSparqlJena(iterations, timeout, queryMap);
        //runStrabon(iterations, timeout, queryMap);
    }

    private static void runGeoSparqlJena(Integer iterations, Duration timeout, HashMap<String, String> queryMap) {
        TestSystemIdentifier testSystemIdentifier = TestSystemIdentifier.GEOSPARQL_JENA;
        File resultsFolder = BenchmarkExecution.GEOSPARQL_JENA_RESULTS;
        BenchmarkExecution.run(testSystemIdentifier, resultsFolder, iterations, timeout, queryMap);
    }

    private static void runStrabon(Integer iterations, Duration timeout, HashMap<String, String> queryMap) {
        TestSystemIdentifier testSystemIdentifier = TestSystemIdentifier.STRABON;
        File resultsFolder = BenchmarkExecution.STRABON_RESULTS;
        BenchmarkExecution.run(testSystemIdentifier, resultsFolder, iterations, timeout, queryMap);
    }

    private static final File DATASET_FOLDER = new File("datasets");

    private static HashMap<String, File> getDatasets() {
        HashMap<String, File> datasetMap = new HashMap<>();
        datasetMap.put(GraphURI.GADM_URI, new File(DATASET_FOLDER, "gag.nt"));
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_FOLDER, "linkedgeodata.nt"));
        datasetMap.put(GraphURI.GEONAMES_URI, new File(DATASET_FOLDER, "geonames.nt"));
        datasetMap.put(GraphURI.HOTSPOTS_URI, new File(DATASET_FOLDER, "hotspots.nt"));
        datasetMap.put(GraphURI.CLC_URI, new File(DATASET_FOLDER, "corine.nt"));
        datasetMap.put(GraphURI.DBPEDIA_URI, new File(DATASET_FOLDER, "dbpedia.nt"));

        return datasetMap;
    }

    private static HashMap<String, File> getTestDatasets() {
        HashMap<String, File> datasetMap = new HashMap<>();
        datasetMap.put(GraphURI.LGD_URI, new File(DATASET_FOLDER, "linkedgeodata.nt"));
        return datasetMap;
    }

    //The Jena TDB Folder and Graph URIs need to be stated in the Assembly File.
    public static final File GEOSPARQL_JENA_TDB_ASSEMBLY_FILE = new File(Main.class.getClassLoader().getResource("geosparql-assemble.ttl").getPath());
    public static final File GEOSPARQL_JENA_TDB_FOLDER = new File("geosparql_jena_tdb");

    private static void assembleRdfsGeosparqlJenaTest() {

        Dataset dataset = TDBFactory.assembleDataset(GEOSPARQL_JENA_TDB_ASSEMBLY_FILE.getAbsolutePath());

        String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        Model model = dataset.getNamedModel(GraphURI.LGD_URI);
        boolean isSchemaLoaded = model.contains(ResourceFactory.createResource(property), RDFS.label);
        LOGGER.info("RDFS Loaded: {}", isSchemaLoaded);

        try (QueryExecution qe = QueryExecutionFactory.create(queryString, dataset)) {
            ResultSet rs = qe.execSelect();
            ResultSetFormatter.outputAsCSV(rs);
        }

    }

    private static void rdfsGeosparqlJenaTest() {

        Dataset dataset = TDBFactory.createDataset(GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath());

        String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        try (QueryExecution qe = QueryExecutionFactory.create(queryString, dataset)) {
            ResultSet rs = qe.execSelect();
            ResultSetFormatter.outputAsCSV(rs);
        }

    }

    private static void exportGeosparqlJenaTest() {
        Dataset dataset = TDBFactory.createDataset(GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath());
        Model model = dataset.getNamedModel(GraphURI.LGD_URI);
        try (FileOutputStream out = new FileOutputStream(new File("lgd-jena.ttl"))) {
            RDFDataMgr.write(out, model, Lang.TTL);
        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }
    }

}
