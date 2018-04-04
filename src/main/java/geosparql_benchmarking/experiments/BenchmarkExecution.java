package geosparql_benchmarking.experiments;

import static geosparql_benchmarking.Main.GEOSPARQL_JENA_TDB_FOLDER;
import geosparql_benchmarking.test_systems.GeosparqlJenaTestSystem;
import geosparql_benchmarking.test_systems.ParliamentTestSystem;
import geosparql_benchmarking.test_systems.StrabonTestSystem;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkExecution {

    final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void warmUp(HashMap<TestSystem, File> testSystemFolders, Integer timeoutSecs, HashMap<String, String> queryMap) {

        for (Entry<TestSystem, File> entry : testSystemFolders.entrySet()) {
            TestSystem testSystem = entry.getKey();
            File resultsFolder = entry.getValue();
            run(testSystem, resultsFolder, 1, timeoutSecs, queryMap);
        }
    }

    public static void runAll(HashMap<TestSystem, File> testSystemFolders, Integer repetitions, Integer timeoutSecs, HashMap<String, String> queryMap) {

        for (Entry<TestSystem, File> entry : testSystemFolders.entrySet()) {
            TestSystem testSystem = entry.getKey();
            File resultsFolder = entry.getValue();
            run(testSystem, resultsFolder, repetitions, timeoutSecs, queryMap);
        }
    }

    public static void run(TestSystem testSystem, File resultsFolder, Integer repetitions, Integer timeoutSecs, HashMap<String, String> queryMap) {

        for (int i = 0; i < repetitions; i++) {
            for (Entry<String, String> entry : queryMap.entrySet()) {
                try {
                    String queryName = entry.getKey();
                    String queryString = entry.getValue();
                    QueryResult queryResult = testSystem.runQueryWithTimeout(queryString, timeoutSecs);
                    if (!queryResult.isCompleted()) {
                        LOGGER.info("System: {}, Query: {} - Did not complete. Skipping repetitions.", testSystem.getName(), queryName);
                        break;
                    }
                } catch (Exception ex) {
                    LOGGER.error("Exception: {}", ex.getMessage());
                }
            }
        }
    }

    public static GeosparqlJenaTestSystem getGeosparqlJena() {
        GeosparqlJenaTestSystem geosparql = new GeosparqlJenaTestSystem(GEOSPARQL_JENA_TDB_FOLDER);
        geosparql.initialize();
        return geosparql;
    }

    public static ParliamentTestSystem getParliament() {
        ParliamentTestSystem parliament = new ParliamentTestSystem();
        parliament.initialize();
        return parliament;
    }

    public static StrabonTestSystem getStrabon() {
        String db = "endpoint";
        String user = "postgres"; //String user = "postgres";
        String password = "postgres"; //String passwd = "postgres";
        Integer port = 5432;
        String host = "localhost"; //"localhost"; //"127.0.0.1"

        StrabonTestSystem strabon = new StrabonTestSystem(db, user, password, port, host);
        strabon.initialize();
        return strabon;
    }

    public static final File RESULTS_FOLDER = new File("results");
    public static final File GEOSPARQL_JENA_RESULTS = new File(RESULTS_FOLDER, "geosparql_jena");
    public static final File PARLIAMENT_RESULTS = new File(RESULTS_FOLDER, "parliament");
    public static final File STRABON_RESULTS = new File(RESULTS_FOLDER, "strabon");

    public static void createResultsFolders() {
        RESULTS_FOLDER.mkdir();
        GEOSPARQL_JENA_RESULTS.mkdir();
        PARLIAMENT_RESULTS.mkdir();
        STRABON_RESULTS.mkdir();
    }

    public static HashMap<TestSystem, File> getTestSystemFolders() {

        HashMap<TestSystem, File> testSystemFolders = new HashMap<>();
        testSystemFolders.put(getGeosparqlJena(), GEOSPARQL_JENA_RESULTS);
        testSystemFolders.put(getParliament(), PARLIAMENT_RESULTS);
        testSystemFolders.put(getStrabon(), STRABON_RESULTS);
        return testSystemFolders;
    }

}
