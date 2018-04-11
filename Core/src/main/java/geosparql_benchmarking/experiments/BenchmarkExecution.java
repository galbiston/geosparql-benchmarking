package geosparql_benchmarking.experiments;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final File RESULTS_FOLDER = new File("../results");

    static {
        RESULTS_FOLDER.mkdir();
    }

    public static final String WARM_RUN_RESULTS_FOLDER_NAME = "warm_run";
    public static final String COLD_RUN_RESULTS_FOLDER_NAME = "cold_run";

    /**
     * Iterate through each query for the test system.<br>
     * An new instance of the test system will be obtained for each query.<br>
     * It is initialised, warmed up by a single query execution that is ignored,
     * then run for the number of iterations before being closed.
     *
     * @param testSystemFactory
     * @param iterations
     * @param timeout
     * @param queryMap
     * @return
     */
    public static final List<IterationResult> runWarm(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {

        String testSystemName = testSystemFactory.getTestSystemName();
        File testSystemResultsFolder = testSystemFactory.getResultsFolder();
        File runResultsFolder = new File(testSystemResultsFolder, WARM_RUN_RESULTS_FOLDER_NAME);
        LOGGER.info("----------Warm Run - System: {}, Folder: {} - Started----------", testSystemName, runResultsFolder);

        List<IterationResult> allIterationResults = new ArrayList<>(queryMap.size() * iterations);
        for (Entry<String, String> entry : queryMap.entrySet()) {
            List<IterationResult> iterationResults = new ArrayList<>(iterations);
            String[] queryLabel = entry.getKey().split("#");
            String queryType = queryLabel[0];
            String queryName = queryLabel[1];
            File resultsFolder = new File(runResultsFolder, queryType);
            resultsFolder.mkdir();
            String queryString = entry.getValue();

            try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
                queryString = testSystem.translateQuery(queryString);
                //Warm Up execution.
                LOGGER.info("----------Warmup Query - System: {}, Type: {}, Query: {}, Started----------", testSystemName, queryType, queryName);
                QueryResult queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                LOGGER.info("----------Warmup Query - System: {}, Type: {}, Query: {},Completed----------", testSystemName, queryType, queryName);

                if (queryResult.isCompleted()) {
                    //Benchmark executions.
                    for (int i = 1; i <= iterations; i++) {
                        LOGGER.info("----------Query Iteration - System: {}, Type: {}, Query: {}, Iteration: {} - Started----------", testSystemName, queryType, queryName, i);
                        queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                        LOGGER.info("----------Query Iteration - System: {}, Type: {}, Query: {}, Iteration: {} - Completed----------", testSystemName, queryType, queryName, i);
                        if (queryResult.isCompleted()) {
                            IterationResult iterationResult = new IterationResult(testSystemName, queryType, queryName, queryString, i, queryResult);
                            iterationResults.add(iterationResult);
                        } else {
                            LOGGER.error("System: {}, Type: {}, Query: {}, Iteration: {} - Did not complete. Skipping remaining iterations.", testSystemName, queryType, queryName, i);
                            break;
                        }
                    }
                } else {
                    LOGGER.error("System: {}, Query: {} - Did not complete warm up. Skipping all iterations.", testSystemName, queryName);
                }
            } catch (Exception ex) {
                LOGGER.error("Exception: {}", ex.getMessage());
            }

            //Write results for all iterations for each query to own file.
            IterationResult.writeResultsFile(resultsFolder, iterationResults);
            allIterationResults.addAll(iterationResults);
        }

        //Write summary for all queries and iterations performed to a single file.
        IterationResult.writeSummaryFile(runResultsFolder, allIterationResults);
        LOGGER.info("----------Warm Run - System: {}, Folder: {} - Completed----------", testSystemFactory.getTestSystemName(), testSystemFactory.getResultsFolder());

        return allIterationResults;
    }

    /**
     * Iterate through each query for the test system.<br>
     * In each iteration a new instance of the test system will be obtained for
     * each query.<br>
     * It is initialised, used for a single query execution before being closed.
     *
     * @param testSystemFactory
     * @param iterations
     * @param timeout
     * @param queryMap
     * @return
     */
    public static final List<IterationResult> runCold(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {

        String testSystemName = testSystemFactory.getTestSystemName();
        File testSystemResultsFolder = testSystemFactory.getResultsFolder();
        File runResultsFolder = new File(testSystemResultsFolder, COLD_RUN_RESULTS_FOLDER_NAME);
        LOGGER.info("----------Cold Run - System: {}, Folder: {} - Started----------", testSystemName, runResultsFolder);

        List<IterationResult> allIterationResults = new ArrayList<>(queryMap.size() * iterations);
        for (Entry<String, String> entry : queryMap.entrySet()) {
            List<IterationResult> iterationResults = new ArrayList<>(iterations);
            String[] queryLabel = entry.getKey().split("#");
            String queryType = queryLabel[0];
            String queryName = queryLabel[1];
            File resultsFolder = new File(runResultsFolder, queryType);
            resultsFolder.mkdir();
            String queryString = entry.getValue();

            //Benchmark executions.
            for (int i = 1; i <= iterations; i++) {

                try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
                    queryString = testSystem.translateQuery(queryString);

                    LOGGER.info("----------Cold Iteration - System: {}, Type: {}, Query: {}, Iteration: {} - Started----------", testSystemName, queryType, queryName, i);
                    QueryResult queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                    LOGGER.info("----------Cold Iteration - System: {}, Type: {}, Query: {}, Iteration: {} - Completed----------", testSystemName, queryType, queryName, i);
                    if (queryResult.isCompleted()) {
                        IterationResult iterationResult = new IterationResult(testSystemName, queryType, queryName, queryString, i, queryResult);
                        iterationResults.add(iterationResult);
                    } else {
                        LOGGER.error("System: {}, Type: {}, Query: {}, Iteration: {} - Did not complete. Skipping remaining iterations.", testSystemName, queryType, queryName, i);
                        break;
                    }
                } catch (Exception ex) {
                    LOGGER.error("Exception: {}", ex.getMessage());
                }
            }

            //Write results for all iterations for each query to own file.
            IterationResult.writeResultsFile(resultsFolder, iterationResults);
            allIterationResults.addAll(iterationResults);
        }

        //Write summary for all queries and iterations performed to a single file.
        IterationResult.writeSummaryFile(runResultsFolder, allIterationResults);
        LOGGER.info("----------Cold Run - System: {}, Folder: {} - Completed----------", testSystemFactory.getTestSystemName(), testSystemFactory.getResultsFolder());

        return allIterationResults;
    }
}
