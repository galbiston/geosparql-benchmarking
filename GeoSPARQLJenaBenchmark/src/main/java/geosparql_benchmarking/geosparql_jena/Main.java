package geosparql_benchmarking.geosparql_jena;

import data_setup.BenchmarkParameters;
import data_setup.DataGeneration;
import data_setup.Dataset_CRS84;
import data_setup.Dataset_Greek_Grid;
import data_setup.Dataset_WGS84;
import data_setup.Dataset_WGS84_Legacy;
import data_setup.GraphURI;
import execution.BenchmarkExecution;
import execution.QueryCase;
import execution.QueryLoader;
import execution.TestSystem;
import execution.TestSystemFactory;
import execution_results.QueryResult;
import implementation.GeoSPARQLSupport;
import implementation.data_conversion.ConvertCRS;
import implementation.data_conversion.GeoSPARQLPredicates;
import implementation.support.GeoSerialisationEnum;
import implementation.vocabulary.SRS_URI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
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
import queries.geographica.MicroBenchmark;
import static queries.geographica.MicroBenchmark.GIVEN_POLYGON;
import static queries.geographica.MicroBenchmark.SPATIAL_SELECTIONS;

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

        TreeMap<String, File> datasetMap = Dataset_WGS84.getAll();

        Boolean inferenceEnabled = true;
        //equalsTest();
        //runPartsTDB(inferenceEnabled);
        //test();
        //convertDatasetCRS();
        //applyPredicates();
        //indexInMemoryTest();
        //indexTDBTest();
        //TDB
        //GeosparqlJenaTDBTestSystemFactory.clearDataset(GEOSPARQL_JENA_TDB_FOLDER);
        //GeosparqlJenaTDBTestSystemFactory.loadDataset(GEOSPARQL_JENA_TDB_FOLDER, datasetMap, inferenceEnabled);
        runTDB(inferenceEnabled);
        //runTestTDB(inferenceEnabled);
        //rdfsJenaTDBTest();
        //In Memory
        //runInMemory(datasetMap, inferenceEnabled);
        //runTestInMemory(datasetMap, inferenceEnabled);
        //No Index
        //runNoIndexTDB(datasetMap, inferenceEnabled);
        //runTestNoIndexTDB(datasetMap, inferenceEnabled);
        //Data Loading
        //runDatasetLoad(tdbTestSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //runDatasetLoad(memTestSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //runDatasetLoad(noIndexTestSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //GeosparqlJenaMemTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        //rdfsJenaMemTest(memDataset);
        //generateGeonamesFile();
        //generatePoints();
    }

    public static void runPartsTDB(Boolean inferenceEnabled) {
        GeosparqlJenaTDBTestSystemFactory testSystemFactory = new GeosparqlJenaTDBTestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query16", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query16.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query17", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query17.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));

        /*
        queryCases.add(new QueryCase("Query7", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query7.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_1)));
        queryCases.add(new QueryCase("Query8", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query8.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query9", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query9.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query10", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query10.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_2)));
        queryCases.add(new QueryCase("Query11", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query11.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query12", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query12.spl").replace("GIVEN_LINE_IN_WKT", GIVEN_LINESTRING_3)));
        queryCases.add(new QueryCase("Query13", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query13.spl").replace("GIVEN_POLYGON_IN_WKT", GIVEN_POLYGON)));
        queryCases.add(new QueryCase("Query14", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS)));
        queryCases.add(new QueryCase("Query15", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query15.spl").replace("GIVEN_POINT_IN_WKT", GIVEN_POINT).replace("GIVEN_RADIUS", GIVEN_RADIUS)));
        queryCases.add(new QueryCase("Query19", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query19.spl")));
        queryCases.add(new QueryCase("Query21", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query21.spl")));
        queryCases.add(new QueryCase("Query23", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query23.spl")));
        queryCases.add(new QueryCase("Query25", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query25.spl")));
        queryCases.add(new QueryCase("Query27", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query27.spl")));
         */
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, queryCases, BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runTDB(Boolean inferenceEnabled) {
        GeosparqlJenaTDBTestSystemFactory testSystemFactory = new GeosparqlJenaTDBTestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runInMemory(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        Dataset memDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaInMemoryTestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        GeosparqlJenaInMemoryTestSystemFactory testSystemFactory = new GeosparqlJenaInMemoryTestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runNoIndexTDB(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        GeosparqlJenaNoIndexTDBTestSystemFactory testSystemFactory = new GeosparqlJenaNoIndexTDBTestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
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

    public static void runTestNoIndexTDB(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        GeosparqlJenaNoIndexTDBTestSystemFactory noIndexTestSystemFactory = new GeosparqlJenaNoIndexTDBTestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME, inferenceEnabled);
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

        File inputFolder = Dataset_WGS84_Legacy.FOLDER;
        Lang inputLanguage = Lang.NTRIPLES;
        File outputFolder = Dataset_Greek_Grid.FOLDER;
        Lang outputLanguage = Lang.NTRIPLES;
        String outputSrsURI = SRS_URI.GREEK_GRID_CRS;
        ConvertCRS.convertFolder(inputFolder, inputLanguage, outputFolder, outputLanguage, outputSrsURI);
    }

    public static void indexInMemoryTest() {
        TreeMap<String, File> datasetMap = Dataset_CRS84.getLinkedGeodata();
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
        TreeMap<String, File> datasetMap = Dataset_CRS84.getLinkedGeodata();
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

        TreeMap<String, File> datasetMap = Dataset_WGS84_Legacy.getAll();
        for (File datasetFile : datasetMap.values()) {

            String outputFilename = datasetFile.getName();
            String parentFoldername = datasetFile.getParentFile().getName();
            //Create folder in current directory.
            File outputFolder = new File(parentFoldername);
            outputFolder.mkdir();

            //Output file will go in the new folder.
            File outputFile = new File(outputFolder, outputFilename);

            GeoSPARQLPredicates.applyFile(datasetFile, Lang.NT, outputFile, Lang.NT);
        }
    }

    public static void generateGeonamesFile() {
        Dataset memDataset = DatasetFactory.createTxnMem();
        Boolean inferenceEnabled = true;
        GeosparqlJenaInMemoryTestSystemFactory.loadDataset(Dataset_WGS84_Legacy.getGeonames(), inferenceEnabled, memDataset);
        GeosparqlJenaInMemoryTestSystemFactory memTestSystemFactory = new GeosparqlJenaInMemoryTestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
        DataGeneration.storeAllGeonames(memTestSystemFactory.getTestSystem(), new File("geonames.txt"));

    }

    public static void generatePoints() {
        DataGeneration.generateGeographicaPoint(100, new File("points.txt"));
    }

    public static void test() {

        Dataset dataset = TDBFactory.createDataset(GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath());
        GeoSPARQLSupport.loadFunctionsMemoryIndex();
        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
                + "PREFIX datasets: <http://geographica.di.uoa.gr/dataset/>                           \n"
                + "PREFIX geonames: <http://www.geonames.org/ontology#>                               \n"
                + "PREFIX opengis: <http://www.opengis.net/def/uom/OGC/1.0/>\n"
                + "                                                                                   \n"
                + "SELECT (geof:buffer(?o1, 4, opengis:metre) AS ?ret) \n"
                + "WHERE {                                                                            \n"
                + "    GRAPH datasets:geonames {?s1 geonames:asWKT ?o1}\n"
                + "}";

        dataset.begin(ReadWrite.READ);
        try (QueryExecution qe = QueryExecutionFactory.create(queryString, dataset)) {
            ResultSet rs = qe.execSelect();
            ResultSetFormatter.outputAsCSV(rs);
        }
        dataset.end();
        dataset.close();

    }

    private static void equalsTest() {

        GeosparqlJenaTDBTestSystemFactory testSystemFactory = new GeosparqlJenaTDBTestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, true);

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/EPSG/0/2100> POINT (474382.14862145175 4203347.7258966705)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
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

    public void convertCRS() {
        System.out.println("convertCRS");

        String geometryLiteral = "<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(37.98 23.71)";
        String outputSrsURI = "http://www.opengis.net/def/crs/EPSG/0/2100";

        String convertedGeometryLiteral = ConvertCRS.convertGeometryLiteral(geometryLiteral, outputSrsURI, GeoSerialisationEnum.WKT);
        System.out.println("Original: " + geometryLiteral);
        System.out.println("Conversion: " + convertedGeometryLiteral);
    }

}
