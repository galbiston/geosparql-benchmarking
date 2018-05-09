package geosparql_benchmarking.geosparql_jena;

import data_setup.BenchmarkParameters;
import data_setup.DatasetSources;
import data_setup.GraphURI;
import execution.BenchmarkExecution;
import data_setup.DataGeneration;
import execution.QueryCase;
import execution.QueryLoader;
import execution.TestSystemFactory;
import queries.geographica.MicroBenchmark;
import implementation.data_conversion.ConvertCRS;
import implementation.data_conversion.GeoSPARQLPredicates;
import implementation.vocabulary.SRS_URI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
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
    public static final String GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME = "geosparql_jena_in_memory";
    public static final String GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME = "geosparql_jena_no_index";
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

        //applyPredicates();
        //indexInMemoryTest();
        //indexTDBTest();
        //TDB
        //GeosparqlJenaTDBTestSystemFactory.clearDataset(GEOSPARQL_JENA_TDB_FOLDER);
        //GeosparqlJenaTDBTestSystemFactory.loadDataset(GEOSPARQL_JENA_TDB_FOLDER, datasetMap, inferenceEnabled);
        //runTDB(inferenceEnabled);
        //runTestTDB(inferenceEnabled);
        //rdfsJenaTDBTest();
        //In Memory
        //runInMemory(datasetMap, inferenceEnabled);
        //runTestInMemory(datasetMap, inferenceEnabled);
        //No Index
        //runNoIndex(datasetMap, inferenceEnabled);
        //runTestNoIndex(datasetMap, inferenceEnabled);
        //Data Loading
        //runDatasetLoad(tdbTestSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //runDatasetLoad(memTestSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //runDatasetLoad(noIndexTestSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //GeosparqlJenaMemTestSystemFactory.loadDataset(DatasetSources.getCRS84TestDatasets(), inferenceEnabled, memDataset);
        //rdfsJenaMemTest(memDataset);
        //generateGeonamesFile();
        //generatePoints();
    }

    public static void runTDB(Boolean inferenceEnabled) {
        GeosparqlJenaTDBTestSystemFactory testSystemFactory = new GeosparqlJenaTDBTestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_CASES, BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runInMemory(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        Dataset memDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaInMemoryTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        GeosparqlJenaInMemoryTestSystemFactory memTestSystemFactory = new GeosparqlJenaInMemoryTestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(memTestSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_CASES, BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runNoIndex(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        Dataset noIndexDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaNoIndexTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, noIndexDataset);
        GeosparqlJenaNoIndexTestSystemFactory noIndexTestSystemFactory = new GeosparqlJenaNoIndexTestSystemFactory(noIndexDataset, GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(noIndexTestSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_CASES, BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runTestTDB(Boolean inferenceEnabled) {
        GeosparqlJenaTDBTestSystemFactory testSystemFactory = new GeosparqlJenaTDBTestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, 1, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadNonTopologicalFunctionsQuery_3(), BenchmarkParameters.RESULT_LINE_LIMIT_5000);
    }

    public static void runTestInMemory(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        Dataset memDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaInMemoryTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        GeosparqlJenaInMemoryTestSystemFactory memTestSystemFactory = new GeosparqlJenaInMemoryTestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(memTestSystemFactory, 1, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadNonTopologicalFunctionsQuery_3(), BenchmarkParameters.RESULT_LINE_LIMIT_5000);
    }

    public static void runTestNoIndex(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        Dataset noIndexDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaNoIndexTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, noIndexDataset);
        GeosparqlJenaNoIndexTestSystemFactory noIndexTestSystemFactory = new GeosparqlJenaNoIndexTestSystemFactory(noIndexDataset, GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(noIndexTestSystemFactory, 1, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadNonTopologicalFunctionsQuery_3(), BenchmarkParameters.RESULT_LINE_LIMIT_5000);
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
        String outputSrsURI = SRS_URI.DEFAULT_WKT_CRS84;
        ConvertCRS.convertFolder(inputFolder, inputLanguage, outputFolder, outputLanguage, outputSrsURI);
    }

    public static void indexInMemoryTest() {
        TreeMap<String, File> datasetMap = DatasetSources.getCRS84TestDatasets();
        Boolean inferenceEnabled = true;
        Dataset memDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaInMemoryTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        GeosparqlJenaInMemoryTestSystemFactory memTestSystemFactory = new GeosparqlJenaInMemoryTestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
        List<QueryCase> queryCases = new ArrayList<>();

        String queryString = "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX dataset: <http://geographica.di.uoa.gr/dataset/>\n"
                + "PREFIX lgd: <http://linkedgeodata.org/ontology/>\n"
                + "SELECT ?object\n"
                + "WHERE{ \n"
                + "GRAPH dataset:lgd {<http://linkedgeodata.org/triplify/way14342611> geo:sfContains ?object}\n"
                + "}";
        queryCases.add(new QueryCase("MemoryIndex", "IndexTesting", queryString));
        BenchmarkExecution.runBoth(memTestSystemFactory, 1, BenchmarkParameters.TIMEOUT, queryCases, BenchmarkParameters.RESULT_LINE_LIMIT_5000);
    }

    public static void indexTDBTest() {
        TreeMap<String, File> datasetMap = DatasetSources.getCRS84TestDatasets();
        Boolean inferenceEnabled = true;
        File datasetFolder = new File("geosparql_jena_tdb_test");
        FileUtils.deleteQuietly(datasetFolder);
        File indexFolder = new File("geosparql_indexes");
        FileUtils.deleteQuietly(indexFolder);
        GeosparqlJenaTDBTestSystemFactory.loadDataset(datasetFolder, datasetMap, inferenceEnabled);
        GeosparqlJenaTDBTestSystemFactory tdbTestSystemFactory = new GeosparqlJenaTDBTestSystemFactory(datasetFolder, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        List<QueryCase> queryCases = new ArrayList<>();

        String queryString = "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
                + "PREFIX dataset: <http://geographica.di.uoa.gr/dataset/>\n"
                + "PREFIX lgd: <http://linkedgeodata.org/ontology/>\n"
                + "SELECT ?object\n"
                + "WHERE{ \n"
                + "GRAPH dataset:lgd {<http://linkedgeodata.org/triplify/way14342611> geo:sfContains ?object}\n"
                + "}";
        queryCases.add(new QueryCase("TDBIndex", "IndexTesting", queryString));
        BenchmarkExecution.runBoth(tdbTestSystemFactory, 1, BenchmarkParameters.TIMEOUT, queryCases, BenchmarkParameters.RESULT_LINE_LIMIT_5000);
    }

    public static void applyPredicates() {

        TreeMap<String, File> datasetMap = DatasetSources.getCRS84TestDatasets();
        for (File datasetFile : datasetMap.values()) {

            File outputFile = new File(datasetFile.getName());
            GeoSPARQLPredicates.applyFile(datasetFile, Lang.NT, outputFile, Lang.NT);
        }
    }

    public static void generateGeonamesFile() {
        Dataset memDataset = DatasetFactory.createTxnMem();
        Boolean inferenceEnabled = true;
        GeosparqlJenaInMemoryTestSystemFactory.loadDataset(DatasetSources.getWGS84LegacyDataset_Geonames(), inferenceEnabled, memDataset);
        GeosparqlJenaInMemoryTestSystemFactory memTestSystemFactory = new GeosparqlJenaInMemoryTestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
        DataGeneration.storeAllGeonames(memTestSystemFactory.getTestSystem(), new File("geonames.txt"));

    }

    public static void generatePoints() {
        DataGeneration.generateGeographicaPoint(100, new File("points.txt"));
    }

}
