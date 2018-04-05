package geosparql_benchmarking.experiments;

import static geosparql_benchmarking.Main.GEOSPARQL_JENA_TDB_FOLDER;
import geosparql_benchmarking.test_systems.GeosparqlJenaTestSystem;
import geosparql_benchmarking.test_systems.ParliamentTestSystem;
import geosparql_benchmarking.test_systems.StrabonTestSystem;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkExecution {

    final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public enum TEST_SYSTEM_IDENTIFIER {
        GEOSPARQL_JENA, PARLIAMENT, STRABON
    }

    /**
     * Iterate through each provided test system by label.
     *
     * @param testSystemFolders
     * @param iterations
     * @param timeout
     * @param queryMap
     */
    public static void runAll(HashMap<TEST_SYSTEM_IDENTIFIER, File> testSystemFolders, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {

        for (Entry<TEST_SYSTEM_IDENTIFIER, File> entry : testSystemFolders.entrySet()) {
            TEST_SYSTEM_IDENTIFIER testSystemIdentifier = entry.getKey();
            File resultsFolder = entry.getValue();
            run(testSystemIdentifier, resultsFolder, iterations, timeout, queryMap);
        }
    }

    /**
     * Iterate through each query for the test system. An new instance of the
     * test system will be obtained for each query. It is initialised, warmed up
     * a single execution that is ignored, then run for the number of iterations
     * before being closed.
     *
     * @param testSystemIdentifier
     * @param resultsFolder
     * @param iterations
     * @param timeout
     * @param queryMap
     */
    public static void run(TEST_SYSTEM_IDENTIFIER testSystemIdentifier, File resultsFolder, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {

        for (Entry<String, String> entry : queryMap.entrySet()) {
            String queryName = entry.getKey();
            String queryString = entry.getValue();
            TestSystem testSystem = getTestSystem(testSystemIdentifier);
            String testSystemName = testSystem.getName();
            try {
                testSystem.initialize();

                //Warm Up execution.
                LOGGER.info("----------System: {}, Query: {}, Warmup - Started----------", testSystemName, queryName);
                QueryResult queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                LOGGER.info("----------System: {}, Query: {}, Warmup - Completed----------", testSystemName, queryName);

                if (queryResult.isCompleted()) {
                    //Benchmark executions.
                    for (int i = 0; i < iterations; i++) {
                        LOGGER.info("----------System: {}, Query: {}, Iteration: {} - Started----------", testSystemName, queryName, i);
                        queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                        LOGGER.info("----------System: {}, Query: {}, Iteration: {} - Completed----------", testSystemName, queryName, i);
                        if (!queryResult.isCompleted()) {
                            LOGGER.error("System: {}, Query: {}, Iteration: {} - Did not complete. Skipping remaining iterations.", testSystemName, queryName, i);
                            break;
                        }

                        //TODO - Handle result. Summary of all queries and iterations in single file labelled by Test System. Place results from each in query in a sub folder.
                    }
                } else {
                    LOGGER.error("System: {}, Query: {} - Did not complete warm up. Skipping all iterations.", testSystem.getName(), queryName);
                }
            } catch (Exception ex) {
                LOGGER.error("Exception: {}", ex.getMessage());
            } finally {
                testSystem.clearCaches();
                testSystem.close();
            }
        }
    }

    public static final TestSystem getTestSystem(TEST_SYSTEM_IDENTIFIER testSystemIdentifier) {

        switch (testSystemIdentifier) {
            case GEOSPARQL_JENA:
                return getGeosparqlJena();
            case PARLIAMENT:
                return getParliament();
            case STRABON:
                return getStrabon();
            default:
                LOGGER.error("Unrecognised Test System Identifier: {}", testSystemIdentifier);
                return null;
        }
    }

    public static final GeosparqlJenaTestSystem getGeosparqlJena() {
        GeosparqlJenaTestSystem geosparql = new GeosparqlJenaTestSystem(GEOSPARQL_JENA_TDB_FOLDER);
        return geosparql;
    }

    public static final ParliamentTestSystem getParliament() {
        return new ParliamentTestSystem();
    }

    public static final StrabonTestSystem getStrabon() {
        String db = "endpoint";
        String user = "postgres"; //String user = "postgres";
        String password = "postgres"; //String passwd = "postgres";
        Integer port = 5432;
        String host = "localhost"; //"localhost"; //"127.0.0.1"

        return new StrabonTestSystem(db, user, password, port, host);
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

    public static HashMap<TEST_SYSTEM_IDENTIFIER, File> getTestSystemFolders() {
        createResultsFolders();
        HashMap<TEST_SYSTEM_IDENTIFIER, File> testSystemFolders = new HashMap<>();
        testSystemFolders.put(TEST_SYSTEM_IDENTIFIER.GEOSPARQL_JENA, GEOSPARQL_JENA_RESULTS);
        testSystemFolders.put(TEST_SYSTEM_IDENTIFIER.PARLIAMENT, PARLIAMENT_RESULTS);
        testSystemFolders.put(TEST_SYSTEM_IDENTIFIER.STRABON, STRABON_RESULTS);
        return testSystemFolders;
    }

}
