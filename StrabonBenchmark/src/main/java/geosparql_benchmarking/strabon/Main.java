package geosparql_benchmarking.strabon;

import data_setup.GraphURI;
import eu.earthobservatory.runtime.postgis.Strabon;
import eu.earthobservatory.utils.Format;
import execution.BenchmarkExecution;
import execution.ExecutionParameters;
import execution.TestSystem;
import execution.TestSystemFactory;
import execution_results.QueryResult;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.TreeMap;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String dbName = "endpoint";
        String user = "postgres";
        String password = "postgres";
        Integer port = 5432;
        String host = "localhost"; //"127.0.0.1"
        String resultsFolder = "strabon";

        String postgresBinPath = "\"C:\\Program Files\\PostgreSQL\\10\\bin\\";
        String postgresDataPath = "\"C:\\Program Files\\PostgreSQL\\10\\data\\\"";

        Boolean inferenceEnabled = true;
        String baseURI = null;
        String format = "NTRIPLES";
        //String format = "RDFXML"; //Use for conformance dataset.
        //Built using PGAdmin tool to create a PostGIS template.
        String databaseTemplate = "template_postgis";

        try {

            StrabonTestSystemFactory testSystemFactory = new StrabonTestSystemFactory(dbName, user, password, port, host, resultsFolder, inferenceEnabled, baseURI, format, postgresBinPath, postgresDataPath, databaseTemplate);

            //StrabonTestSystemFactory.clearDataset(testSystemFactory);
            //StrabonTestSystemFactory.loadDataset(Dataset_Conformance.getConformanceData(), testSystemFactory);
            //equalsTest3(testSystemFactory);
            ExecutionParameters parameters = ExecutionParameters.extract(args);
            BenchmarkExecution.runType(testSystemFactory, parameters);

        } catch (Exception ex) {
            LOGGER.error("{} for arguments {}", ex.getMessage(), args);
        }
        /*
        //runDatasetLoad(testSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);

        //Strabon
        //BenchmarkExecution.runBoth(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, MicroBenchmark.loadMainQuerySet(), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
        //List<QueryCase> queryCases = MicroBenchmark.loadMainQuerySet();
        //BenchmarkExecution.runWarm(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, queryCases.subList(17, queryCases.size()), BenchmarkParameters.RESULT_LINE_LIMIT_ZERO);
        //rdfsStrabonTest(testSystemFactory);
        //equalsTest(testSystemFactory);
        equalsTestA(testSystemFactory);
        //equalsTest2(testSystemFactory);
        //Data Loading
        //StrabonTestSystemFactory.clearDataset(testSystemFactory);
        //StrabonTestSystemFactory.loadDataset(datasetMap, testSystemFactory);
         */
    }

    public static void runDatasetLoad(TestSystemFactory testSystemFactory, Integer iterations, TreeMap<String, File> datasetMap) {
        BenchmarkExecution.runDatasetLoad(testSystemFactory, iterations, datasetMap);
    }

    private static void rdfsStrabonTest(StrabonTestSystemFactory testSystemFactory) {

        //String property = "<http://www.opengis.net/ont/geosparql#asWKT>";
        String property = "<http://linkedgeodata.org/ontology/asWKT>";
        String queryString = "SELECT ?sub ?obj WHERE{ GRAPH <" + GraphURI.LGD_URI + "> { ?sub " + property + " ?obj}}LIMIT 1";
        //String queryString = "SELECT ?sub ?obj WHERE{ ?sub " + property +  " ?obj}LIMIT 1";

        //Strabon doesn't seem to apply RDFS inferencing even though ahs a paraemter when data loading.
        //Geographica benchmarking paper (page 10) and running this query show it doesn't.
        try (StrabonTestSystem strabonTestSystem = testSystemFactory.getStrabonTestSystem()) {
            Strabon strabon = strabonTestSystem.getStrabon();
            TupleQuery tupleQuery = (TupleQuery) strabon.query(queryString, Format.TUQU, strabon.getSailRepoConnection(), System.out);
            SPARQLResultsCSVWriter csvWriter = new SPARQLResultsCSVWriter(System.out);
            tupleQuery.evaluate(csvWriter);

        } catch (MalformedQueryException | QueryEvaluationException | TupleQueryResultHandlerException | IOException ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest(TestSystemFactory testSystemFactory) {

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

    private static void equalsTestA(TestSystemFactory testSystemFactory) {

        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "SELECT ?res WHERE{"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POINT (23.71 37.98)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?first)"
                + "BIND(\"<http://www.opengis.net/def/crs/OGC/1.3/CRS84> POINT(23.71 37.98)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> AS ?second)"
                + "BIND(geof:sfEquals(?first, ?second) AS ?res) "
                + "}";

        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

    private static void equalsTest2(TestSystemFactory testSystemFactory) {

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

        //This query returns everything when it should just return LineStringD and LineStringD1.
        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
                + "SELECT ?res WHERE{"
                + "GRAPH <http://example.org/dataset#conformance>{"
                + "<http://example.org/Geometry#LineStringD> geo:asWKT ?first ."
                + "?res geo:asWKT ?second ."
                + "FILTER(geof:sfEquals(?first, ?second)) "
                + "}"
                + "}";
        /*
        String queryString = "PREFIX geof: <http://www.opengis.net/def/function/geosparql/> "
                + "PREFIX geo: <http://www.opengis.net/ont/geosparql#>"
                + "PREFIX geom: <http://example.org/Geometry#>"
                + "PREFIX sf: <http://www.opengis.net/ont/sf#>"
                + "SELECT ?res WHERE{"
                + "GRAPH <http://example.org/dataset#conformance>{"
                + "?line a sf:LineString ."
                + "?line geo:asWKT ?first ."
                + "?res a sf:LineString ."
                + "?res geo:asWKT ?second ."
                + "FILTER(geof:sfEquals(?first, ?second)) "
                + "}"
                + "}";
         */
        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            QueryResult qResult = BenchmarkExecution.runQueryWithTimeout(testSystem, queryString, Duration.ofHours(1));
            System.out.println(qResult.getResults());

        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

    }

}
