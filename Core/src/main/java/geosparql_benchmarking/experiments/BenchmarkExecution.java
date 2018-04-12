package geosparql_benchmarking.experiments;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
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
    public static final void runWarm(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {

        String testSystemName = testSystemFactory.getTestSystemName();
        String testTimestamp = LocalDateTime.now().format(IterationResult.FILE_DATE_TIME_FORMAT);
        File testSystemResultsFolder = testSystemFactory.getResultsFolder();
        File runResultsFolder = new File(testSystemResultsFolder, WARM_RUN_RESULTS_FOLDER_NAME);
        runResultsFolder.mkdir();
        LOGGER.info("------Warm Run - System: {}, Folder: {} - Started------", testSystemName, runResultsFolder);

        for (Entry<String, String> entry : queryMap.entrySet()) {
            List<IterationResult> iterationResults = new ArrayList<>(iterations);
            String[] queryLabel = entry.getKey().split("#");
            String queryType = queryLabel[0];
            String queryName = queryLabel[1];
            File resultsFolder = new File(runResultsFolder, queryType);
            resultsFolder.mkdir();
            String queryString = entry.getValue();

            long initStartNanoTime = System.nanoTime();
            try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
                long initEndNanoTime = System.nanoTime();
                queryString = testSystem.translateQuery(queryString);
                //Warm Up execution.
                LOGGER.info("------Warmup Query - System: {}, Type: {}, Query: {}, Started------", testSystemName, queryType, queryName);
                QueryResult queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                LOGGER.info("------Warmup Query - System: {}, Type: {}, Query: {},Completed------", testSystemName, queryType, queryName);

                if (queryResult.isCompleted()) {
                    //Benchmark executions.
                    for (int i = 1; i <= iterations; i++) {
                        LOGGER.info("------Query Iteration - System: {}, Type: {}, Query: {}, Iteration: {} - Started------", testSystemName, queryType, queryName, i);
                        queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                        LOGGER.info("------Query Iteration - System: {}, Type: {}, Query: {}, Iteration: {} - Completed------", testSystemName, queryType, queryName, i);
                        if (queryResult.isCompleted()) {
                            IterationResult iterationResult = new IterationResult(testSystemName, queryType, queryName, queryString, i, queryResult, initStartNanoTime, initEndNanoTime);
                            //Write results for all iterations for each query to own file.
                            IterationResult.writeResultsFile(resultsFolder, iterationResult, queryResult, testTimestamp);
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

            //Write summary for all queries and iterations performed to a single file.
            IterationResult.writeSummaryFile(runResultsFolder, iterationResults, testTimestamp);
        }

        LOGGER.info("------Warm Run - System: {}, Folder: {} - Completed------", testSystemFactory.getTestSystemName(), testSystemFactory.getResultsFolder());

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
    public static final void runCold(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {

        String testSystemName = testSystemFactory.getTestSystemName();
        String testTimestamp = LocalDateTime.now().format(IterationResult.FILE_DATE_TIME_FORMAT);
        File testSystemResultsFolder = testSystemFactory.getResultsFolder();
        File runResultsFolder = new File(testSystemResultsFolder, COLD_RUN_RESULTS_FOLDER_NAME);
        runResultsFolder.mkdir();
        LOGGER.info("------Cold Run - System: {}, Folder: {} - Started------", testSystemName, runResultsFolder);

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
                long initStartNanoTime = System.nanoTime();
                try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
                    long initEndNanoTime = System.nanoTime();
                    queryString = testSystem.translateQuery(queryString);

                    LOGGER.info("------Cold Iteration - System: {}, Type: {}, Query: {}, Iteration: {} - Started------", testSystemName, queryType, queryName, i);
                    QueryResult queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                    LOGGER.info("------Cold Iteration - System: {}, Type: {}, Query: {}, Iteration: {} - Completed------", testSystemName, queryType, queryName, i);
                    if (queryResult.isCompleted()) {
                        IterationResult iterationResult = new IterationResult(testSystemName, queryType, queryName, queryString, i, queryResult, initStartNanoTime, initEndNanoTime);
                        //Write results for all iterations for each query to own file.
                        IterationResult.writeResultsFile(resultsFolder, iterationResult, queryResult, testTimestamp);
                        iterationResults.add(iterationResult);
                    } else {
                        LOGGER.error("System: {}, Type: {}, Query: {}, Iteration: {} - Did not complete. Skipping remaining iterations.", testSystemName, queryType, queryName, i);
                        break;
                    }
                } catch (Exception ex) {
                    LOGGER.error("Exception: {}", ex.getMessage());
                }
            }

            //Write summary for all queries and iterations performed to a single file.
            IterationResult.writeSummaryFile(runResultsFolder, iterationResults, testTimestamp);
        }

        LOGGER.info("------Cold Run - System: {}, Folder: {} - Completed------", testSystemFactory.getTestSystemName(), testSystemFactory.getResultsFolder());
    }

    public static final List<DatasetLoadResult> runDatasetLoad(TestSystemFactory testSystemFactory, Integer iterations, HashMap<String, File> datasetMap) {

        List<DatasetLoadResult> datasetLoadResults = new ArrayList<>();
        File resultsFolder = testSystemFactory.getResultsFolder();
        for (int i = 1; i <= iterations; i++) {
            LOGGER.info("------Dataset Load Run- System: {}, Iteration: {} - Started------", testSystemFactory.getTestSystemName(), i);
            boolean isClear = testSystemFactory.clearDataset();
            if (isClear) {
                DatasetLoadResult datasetLoadResult = testSystemFactory.loadDataset(datasetMap, i);
                datasetLoadResults.add(datasetLoadResult);
            }
            LOGGER.info("------Dataset Load Run- System: {}, Iteration: {} - Completed------", testSystemFactory.getTestSystemName(), i);
        }

        DatasetLoadResult.writeResultsFile(resultsFolder, datasetLoadResults);

        return datasetLoadResults;
    }

}
