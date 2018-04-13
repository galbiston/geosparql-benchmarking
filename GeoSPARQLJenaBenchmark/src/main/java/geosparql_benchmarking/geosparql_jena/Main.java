package geosparql_benchmarking.geosparql_jena;

import geosparql_benchmarking.BenchmarkParameters;
import geosparql_benchmarking.DatasetSources;
import geosparql_benchmarking.GraphURI;
import geosparql_benchmarking.experiments.BenchmarkExecution;
import geosparql_benchmarking.experiments.QueryLoader;
import geosparql_benchmarking.experiments.TestSystemFactory;
import implementation.CRSRegistry;
import implementation.ConvertCRS;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.TreeMap;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final File GEOSPARQL_JENA_TDB_FOLDER = new File("geosparql_jena_tdb");
    public static final String GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME = "geosparql_jena_tdb";
    public static final String GEOSPARL_JENA_MEM_RESULTS_FOLDER_NAME = "geosparql_jena_mem";
    public static final File GEOSPARQL_SCHEMA_FILE = new File("geosparql_vocab_all.rdf");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //TreeMap<String, File> datasetMap = DatasetSources.getCRS84TestDatasets();
        TreeMap<String, File> datasetMap = DatasetSources.getCRS84Datasets();
        //TreeMap<String, File> datasetMap = DatasetSources.getWGS84LegacyDatasets();
        //TreeMap<String, File> datasetMap = DatasetSources.getWGS84LegacyTestDatasets();
        Boolean inferenceEnabled = true;

        //TDB
        //GeosparqlJenaTDBTestSystemFactory.clearDataset(GEOSPARQL_JENA_TDB_FOLDER);
        //GeosparqlJenaTDBTestSystemFactory.loadDataset(GEOSPARQL_JENA_TDB_FOLDER, datasetMap, inferenceEnabled);
        runTDB(inferenceEnabled);
        //runTestTDB(inferenceEnabled);
        //rdfsJenaTDBTest();

        //Memory
        runMem(datasetMap, inferenceEnabled);
        //runMemTest(datasetMap, inferenceEnabled);

        //Data Loading
        //runDatasetLoad(testSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //runDatasetLoad(memTestSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //GeosparqlJenaMemTestSystemFactory.loadDataset(DatasetSources.getCRS84TestDatasets(), inferenceEnabled, memDataset);
        //rdfsJenaMemTest(memDataset);
    }

    public static void runTDB(Boolean inferenceEnabled) {
        GeosparqlJenaTDBTestSystemFactory testSystemFactory = new GeosparqlJenaTDBTestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_MAP, BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runMem(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        Dataset memDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaMemTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        GeosparqlJenaMemTestSystemFactory memTestSystemFactory = new GeosparqlJenaMemTestSystemFactory(memDataset, GEOSPARL_JENA_MEM_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(memTestSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_MAP, BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runTestTDB(Boolean inferenceEnabled) {
        GeosparqlJenaTDBTestSystemFactory testSystemFactory = new GeosparqlJenaTDBTestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, 1, BenchmarkParameters.TIMEOUT, QueryLoader.loadNonTopologicalFunctionsQuery_3(), BenchmarkParameters.RESULT_LINE_LIMIT_5000);
    }

    public static void runTestMem(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        Dataset memDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaMemTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        GeosparqlJenaMemTestSystemFactory memTestSystemFactory = new GeosparqlJenaMemTestSystemFactory(memDataset, GEOSPARL_JENA_MEM_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(memTestSystemFactory, 1, BenchmarkParameters.TIMEOUT, QueryLoader.loadNonTopologicalFunctionsQuery_3(), BenchmarkParameters.RESULT_LINE_LIMIT_5000);
    }

    public static void runDatasetLoad(TestSystemFactory testSystemFactory, Integer iterations, TreeMap<String, File> datasetMap) {
        BenchmarkExecution.runDatasetLoad(testSystemFactory, iterations, datasetMap);
    }

    private static void rdfsJenaTDBTest() {

        Dataset dataset = TDBFactory.createDataset(GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath());

        String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        dataset.begin(ReadWrite.READ);
        try (QueryExecution qe = QueryExecutionFactory.create(queryString, dataset)) {
            ResultSet rs = qe.execSelect();
            ResultSetFormatter.outputAsCSV(rs);
        }
        dataset.end();
        dataset.close();

    }

    private static void rdfsJenaMemTest(Dataset dataset) {

        String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        //String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        dataset.begin(ReadWrite.READ);
        try (QueryExecution qe = QueryExecutionFactory.create(queryString, dataset)) {
            ResultSet rs = qe.execSelect();
            ResultSetFormatter.outputAsCSV(rs);
        }
        dataset.end();
        dataset.close();

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

    /**
     * Parliament 2.7.10 does not accept the original WGS84 coordinate reference
     * system URI present in Geographica datasets.<br>
     * This methods converts the datasets from
     */
    public static void convertDatasetCRS() {

        File inputFolder = DatasetSources.DATASET_WGS84_LEGACY_FOLDER;
        Lang inputLanguage = Lang.NTRIPLES;
        File outputFolder = DatasetSources.DATASET_CRS84_FOLDER;
        Lang outputLanguage = Lang.NTRIPLES;
        String outputSrsURI = CRSRegistry.DEFAULT_WKT_CRS84;
        ConvertCRS.convertFolder(inputFolder, inputLanguage, outputFolder, outputLanguage, outputSrsURI);
    }
}
