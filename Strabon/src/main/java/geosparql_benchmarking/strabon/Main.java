package geosparql_benchmarking.strabon;

import eu.earthobservatory.runtime.postgis.Strabon;
import eu.earthobservatory.utils.Format;
import geosparql_benchmarking.BenchmarkParameters;
import geosparql_benchmarking.DatasetSources;
import geosparql_benchmarking.GraphURI;
import geosparql_benchmarking.experiments.BenchmarkExecution;
import geosparql_benchmarking.experiments.QueryLoader;
import geosparql_benchmarking.experiments.TestSystemFactory;
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
        String user = "postgres"; //String user = "postgres";
        String password = "postgres"; //String passwd = "postgres";
        Integer port = 5432;
        String host = "localhost"; //"localhost"; //"127.0.0.1"
        String resultsFolder = "strabon";

        String postgresBinPath = "\"C:\\Program Files\\PostgreSQL\\10\\bin\\";
        String postgresDataPath = "\"C:\\Program Files\\PostgreSQL\\10\\data\\\"";

        Boolean inferenceEnabled = true;
        String baseURI = null;
        String format = "NTRIPLES";

        //Built using PGAdmin tool to create a PostGIS template.
        String databaseTemplate = "template_postgis";

        StrabonTestSystemFactory testSystemFactory = new StrabonTestSystemFactory(dbName, user, password, port, host, resultsFolder, inferenceEnabled, baseURI, format, postgresBinPath, postgresDataPath, databaseTemplate);

        TreeMap<String, File> datasetMap = DatasetSources.getCRS84Datasets();
        //TreeMap<String, File> datasetMap = DatasetSources.getWGS84LegacyDatasets();

        //runDatasetLoad(testSystemFactory, BenchmarkParameters.ITERATIONS, datasetMap);
        //StrabonTestSystemFactory.clearDataset(testSystemFactory);
        //StrabonTestSystemFactory.loadDataset(datasetMap, testSystemFactory);
        //Back up made: https://www.postgresql.org/docs/10/static/backup-dump.html
        //rdfsStrabonTest(testSystemFactory);
        //runStrabon(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, BenchmarkParameters.QUERY_MAP);
        runStrabon(testSystemFactory, BenchmarkParameters.ITERATIONS, BenchmarkParameters.TIMEOUT, QueryLoader.loadNonTopologicalFunctionsQuery_3());
    }

    public static void runDatasetLoad(TestSystemFactory testSystemFactory, Integer iterations, TreeMap<String, File> datasetMap) {
        BenchmarkExecution.runDatasetLoad(testSystemFactory, iterations, datasetMap);
    }

    public static void runStrabon(StrabonTestSystemFactory testSystemFactory, Integer iterations, Duration timeout, TreeMap<String, String> queryMap) {
        BenchmarkExecution.runWarm(testSystemFactory, iterations, timeout, queryMap);
        BenchmarkExecution.runCold(testSystemFactory, iterations, timeout, queryMap);
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

}
