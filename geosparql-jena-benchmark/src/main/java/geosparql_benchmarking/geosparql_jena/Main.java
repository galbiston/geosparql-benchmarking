/**
 * Copyright 2018 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geosparql_benchmarking.geosparql_jena;

import data_setup.BenchmarkParameters;
import data_setup.DataGeneration;
import data_setup.Dataset_CRS84;
import data_setup.Dataset_WGS84_Legacy;
import data_setup.GraphURI;
import execution.BenchmarkExecution;
import execution.QueryCase;
import execution.QueryLoader;
import execution.TestSystem;
import execution.TestSystemFactory;
import execution_results.QueryResult;
import geosparql_benchmarking.geosparql_jena.cli.JenaExecutionParameters;
import io.github.galbiston.geosparql_jena.configuration.GeoSPARQLConfig;
import io.github.galbiston.geosparql_jena.configuration.GeoSPARQLOperations;
import io.github.galbiston.geosparql_jena.implementation.data_conversion.ConvertData;
import io.github.galbiston.geosparql_jena.implementation.datatype.WKTDatatype;
import io.github.galbiston.geosparql_jena.implementation.vocabulary.SRS_URI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
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
import static queries.geographica.MicroBenchmark.SPATIAL_SELECTIONS;
import static queries.geographica.MicroBenchmark.loadQueryResources;
import queries.geographica.QueryFormat;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final File GEOSPARQL_JENA_TDB_FOLDER = new File("geosparql_jena_tdb");
    public static final String GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME = "geosparql_jena_tdb";
    public static final String GEOSPARL_JENA_TDB_UNION_RESULTS_FOLDER_NAME = "geosparql_jena_tdb_union";
    public static final String GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME = "geosparql_jena_in_memory";
    public static final String GEOSPARL_JENA_IN_MEMORY_UNION_RESULTS_FOLDER_NAME = "geosparql_jena_in_memory_union";
    public static final String GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME = "geosparql_jena_no_index";
    public static final File GEOSPARQL_SCHEMA_FILE = new File("geosparql_vocab_all_v1_0_1_updated.rdf");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //args = new String[]{"tdb_union", "micro_union", "both"};

        try {

            JenaExecutionParameters parameters = JenaExecutionParameters.extract("GeoSPARQL", args);
            TestSystemFactory testSystemFactory = buildTestSystemFactory(parameters);
            BenchmarkExecution.runType(testSystemFactory, parameters);

        } catch (Exception ex) {
            LOGGER.error("{} for arguments {}", ex.getMessage(), args);
        }

        //equalsTest();
        //equalsTest2();
        //intersectsTest();
        //runPartsTDB(inferenceEnabled);
        //test();
        //convertDatasetCRS();
        //applyPredicates();
        //indexInMemoryTest();
        //indexTDBTest();
        //TDB
        //GeosparqlJenaTDBTestSystemFactory.clearDataset(GEOSPARQL_JENA_TDB_FOLDER);
        //TreeMap<String, File> datasetMap = Dataset_Conformance.getConformanceData();
        //TreeMap<String, File> datasetMap = Dataset_CRS84.getAll();
        //GeosparqlJenaTDBTestSystemFactory.loadDataset(GEOSPARQL_JENA_TDB_FOLDER, datasetMap, true);
        //GeosparqlJenaTDBTestSystemFactory testSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, true);
        //equalsTest4(testSystemFactory);
        //runTDB(inferenceEnabled);
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

    public static TestSystemFactory buildTestSystemFactory(JenaExecutionParameters parameters) {
        SystemType systemType = parameters.getSystemType();
        TestSystemFactory testSystemFactory;
        switch (systemType) {
            case TDB:
                testSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
            case TDB_UNION:
                testSystemFactory = new GeosparqlJenaTDBUnion_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_UNION_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
            case NO_INDEX:
                testSystemFactory = new GeosparqlJenaTDBNoIndex_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
            case MEMORY_UNION:
                Dataset memDataset = DatasetFactory.createTxnMem();
                GeosparqlJenaInMemoryUnion_TestSystemFactory.loadDataset(parameters.getDatasetMap(), parameters.getInferenceEnabled(), memDataset);
                testSystemFactory = new GeosparqlJenaInMemoryUnion_TestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_UNION_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
            default:
                memDataset = DatasetFactory.createTxnMem();
                GeosparqlJenaInMemory_TestSystemFactory.loadDataset(parameters.getDatasetMap(), parameters.getInferenceEnabled(), memDataset);
                testSystemFactory = new GeosparqlJenaInMemory_TestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, parameters.getInferenceEnabled());
                break;
        }

        return testSystemFactory;
    }

    public static void runPartsTDB(Boolean inferenceEnabled) {
        GeosparqlJenaTDB_TestSystemFactory testSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        HashMap<String, String> queryResources = loadQueryResources(QueryFormat.PUBLISHED);
        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.add(new QueryCase("Query16", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query16.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query17", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query17.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));

        /*
        queryCases.add(new QueryCase("Query7", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query7.spl").replace("GIVEN_LINE_IN_WKT", queryResources.get("GIVEN_LINESTRING_1"))));
        queryCases.add(new QueryCase("Query8", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query8.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query9", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query9.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query10", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query10.spl").replace("GIVEN_LINE_IN_WKT", queryResources.get("GIVEN_LINESTRING_2"))));
        queryCases.add(new QueryCase("Query11", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query11.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query12", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query12.spl").replace("GIVEN_LINE_IN_WKT", queryResources.get("GIVEN_LINESTRING_3"))));
        queryCases.add(new QueryCase("Query13", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query13.spl").replace("GIVEN_POLYGON_IN_WKT", queryResources.get("GIVEN_POLYGON"))));
        queryCases.add(new QueryCase("Query14", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query14.spl").replace("GIVEN_POINT_IN_WKT", queryResources.get("GIVEN_POINT")).replace("GIVEN_RADIUS", queryResources.get("GIVEN_RADIUS"))));
        queryCases.add(new QueryCase("Query15", "SpatialSelections", QueryLoader.readFile(SPATIAL_SELECTIONS + "/Query15.spl").replace("GIVEN_POINT_IN_WKT", queryResources.get("GIVEN_POINT")).replace("GIVEN_RADIUS", queryResources.get("GIVEN_RADIUS"))));
        queryCases.add(new QueryCase("Query19", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query19.spl")));
        queryCases.add(new QueryCase("Query21", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query21.spl")));
        queryCases.add(new QueryCase("Query23", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query23.spl")));
        queryCases.add(new QueryCase("Query25", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query25.spl")));
        queryCases.add(new QueryCase("Query27", "SpatialJoins", QueryLoader.readFile(SPATIAL_JOINS + "/Query27.spl")));
         */
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, queryCases, BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runTDB(Boolean inferenceEnabled) {
        GeosparqlJenaTDB_TestSystemFactory testSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(QueryFormat.PUBLISHED), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
        //BenchmarkExecution.runCold(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runInMemory(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        Dataset memDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaInMemory_TestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        GeosparqlJenaInMemory_TestSystemFactory testSystemFactory = new GeosparqlJenaInMemory_TestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(QueryFormat.PUBLISHED), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runNoIndexTDB(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        GeosparqlJenaTDBNoIndex_TestSystemFactory testSystemFactory = new GeosparqlJenaTDBNoIndex_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(QueryFormat.PUBLISHED), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
    }

    public static void runTestTDB(Boolean inferenceEnabled) {
        GeosparqlJenaTDB_TestSystemFactory testSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(testSystemFactory, 1, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadNonTopologicalFunctionsQuery_3(), BenchmarkParameters.RESULT_LINE_LIMIT_5000);
    }

    public static void runTestInMemory(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        Dataset memDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaInMemory_TestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        GeosparqlJenaInMemory_TestSystemFactory memTestSystemFactory = new GeosparqlJenaInMemory_TestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
        BenchmarkExecution.runBoth(memTestSystemFactory, 1, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadNonTopologicalFunctionsQuery_3(), BenchmarkParameters.RESULT_LINE_LIMIT_5000);
    }

    public static void runTestNoIndexTDB(TreeMap<String, File> datasetMap, Boolean inferenceEnabled) {
        GeosparqlJenaTDBNoIndex_TestSystemFactory noIndexTestSystemFactory = new GeosparqlJenaTDBNoIndex_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_NO_INDEX_RESULTS_FOLDER_NAME, inferenceEnabled);
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
        File outputFolder = Dataset_CRS84.FOLDER;
        Lang outputLanguage = Lang.NTRIPLES;
        String outputSrsURI = SRS_URI.DEFAULT_WKT_CRS84;
        ConvertData.convertFolder(inputFolder, inputLanguage, outputFolder, outputLanguage, outputSrsURI);
    }

    public static void indexInMemoryTest() {
        TreeMap<String, File> datasetMap = Dataset_CRS84.getLinkedGeodata();
        Boolean inferenceEnabled = true;
        Dataset memDataset = DatasetFactory.createTxnMem();
        GeosparqlJenaInMemory_TestSystemFactory.loadDataset(datasetMap, inferenceEnabled, memDataset);
        GeosparqlJenaInMemory_TestSystemFactory memTestSystemFactory = new GeosparqlJenaInMemory_TestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
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
        GeosparqlJenaTDB_TestSystemFactory.loadDataset(datasetFolder, datasetMap, inferenceEnabled);
        GeosparqlJenaTDB_TestSystemFactory tdbTestSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(datasetFolder, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, inferenceEnabled);
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

            GeoSPARQLOperations.applyDefaultGeometry(datasetFile, Lang.NT, outputFile, Lang.NT);
        }
    }

    public static void generateGeonamesFile() {
        Dataset memDataset = DatasetFactory.createTxnMem();
        Boolean inferenceEnabled = true;
        GeosparqlJenaInMemory_TestSystemFactory.loadDataset(Dataset_WGS84_Legacy.getGeonames(), inferenceEnabled, memDataset);
        GeosparqlJenaInMemory_TestSystemFactory memTestSystemFactory = new GeosparqlJenaInMemory_TestSystemFactory(memDataset, GEOSPARL_JENA_IN_MEMORY_RESULTS_FOLDER_NAME, inferenceEnabled);
        DataGeneration.storeAllGeonames(memTestSystemFactory.getTestSystem(), new File("geonames.txt"));

    }

    public static void generatePoints() {
        DataGeneration.generateGeographicaPoint(100, new File("points.txt"));
    }

    public static void test() {

        Dataset dataset = TDBFactory.createDataset(GEOSPARQL_JENA_TDB_FOLDER.getAbsolutePath());
        GeoSPARQLConfig.setupMemoryIndex();
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

        GeosparqlJenaTDB_TestSystemFactory testSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, true);

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POINT (23.71 37.98)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
                + "BIND(\"<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(37.98 23.71)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?second)"
                + "BIND(geof:sfEquals(?first, ?second) AS ?res) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest2() {

        GeosparqlJenaTDB_TestSystemFactory testSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, true);

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> LINESTRING(0 0, 2 0, 5 0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> LINESTRING(5 0, 0 0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?second)"
                + "BIND(geof:sfEquals(?first, ?second) AS ?res) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest3(TestSystemFactory testSystemFactory) {

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
                + "SELECT ?res WHERE{"
                + "GRAPH <http://example.org/dataset#conformance>{"
                + "<http://example.org/Geometry#LineStringD> geo:asWKT ?first ."
                + "?res geo:asWKT ?second ."
                + "FILTER(geof:sfEquals(?first, ?second)) "
                + "}"
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest4(TestSystemFactory testSystemFactory) {

        //This query returns everything when it should just return LineStringD and LineStringD1.
        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
                + "SELECT ?res WHERE{"
                + "GRAPH <http://example.org/dataset#conformance>{"
                + "?res geo:asWKT ?first ."
                + "}"
                + "GRAPH <http://example.org/dataset#conformance-equals>{"
                + "?secondGeo geo:asWKT ?second ."
                + "}"
                + "FILTER(geof:sfEquals(?first, ?second)) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void intersectsTest() {

        GeosparqlJenaTDB_TestSystemFactory testSystemFactory = new GeosparqlJenaTDB_TestSystemFactory(GEOSPARQL_JENA_TDB_FOLDER, GEOSPARL_JENA_TDB_RESULTS_FOLDER_NAME, true);

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> LINESTRING(1 1, 1 -1)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> LINESTRING(5 0, 0 0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?second)"
                + "BIND(geof:sfIntersects(?first, ?second) AS ?res) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    public void convertCRS() {
        System.out.println("convertCRS");

        String geometryLiteral = "<http://www.opengis.net/def/crs/EPSG/0/4326> POINT(37.98 23.71)";
        String outputSrsURI = "http://www.opengis.net/def/crs/EPSG/0/2100";

        String convertedGeometryLiteral = ConvertData.convertGeometryLiteral(geometryLiteral, outputSrsURI, WKTDatatype.INSTANCE);
        System.out.println("Original: " + geometryLiteral);
        System.out.println("Conversion: " + convertedGeometryLiteral);
    }

}
