package geosparql_benchmarking.experiments;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
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
    public static final void runWarm(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, TreeMap<String, String> queryMap) {

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
                LOGGER.info("------Warmup Query - System: {}, Query: {}, Type: {} - Started------", testSystemName, queryName, queryType);
                testSystem.runQueryWithTimeout(queryString, timeout);
                LOGGER.info("------Warmup Query - System: {}, Query: {}, Type: {} - Completed------", testSystemName, queryName, queryType);

                //Benchmark executions.
                for (int i = 1; i <= iterations; i++) {
                    LOGGER.info("------Query Iteration - System: {}, Query: {}, Type: {}, Iteration: {} - Started------", testSystemName, queryName, queryType, i);
                    QueryResult queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                    LOGGER.info("------Query Iteration - System: {}, Query: {}, Type: {}, Iteration: {} - Completed------", testSystemName, queryName, queryType, i);

                    IterationResult iterationResult = new IterationResult(testSystemName, queryType, queryName, queryString, i, queryResult, initStartNanoTime, initEndNanoTime);
                    //Write results for all iterations for each query to own file.
                    IterationResult.writeResultsFile(resultsFolder, iterationResult, queryResult, testTimestamp);
                    iterationResults.add(iterationResult);
                    if (!queryResult.isCompleted()) {
                        LOGGER.warn("System: {}, Query: {}, Type: {},  Iteration: {} - Did not complete.", testSystemName, queryName, queryType, i);
                        break;
                    }
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
    public static final void runCold(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, TreeMap<String, String> queryMap) {

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

                    LOGGER.info("------Cold Iteration - System: {}, Query: {}, Type: {}, Iteration: {} - Started------", testSystemName, queryName, queryType, i);
                    QueryResult queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                    LOGGER.info("------Cold Iteration - System: {}, Query: {}, Type: {}, Iteration: {} - Completed------", testSystemName, queryName, queryType, i);

                    IterationResult iterationResult = new IterationResult(testSystemName, queryType, queryName, queryString, i, queryResult, initStartNanoTime, initEndNanoTime);
                    //Write results for all iterations for each query to own file.
                    IterationResult.writeResultsFile(resultsFolder, iterationResult, queryResult, testTimestamp);
                    iterationResults.add(iterationResult);
                    if (!queryResult.isCompleted()) {
                        LOGGER.warn("System: {}, Query: {}, Type: {}, Iteration: {} - Did not complete.", testSystemName, queryName, queryType, i);
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

    public static final List<DatasetLoadResult> runDatasetLoad(TestSystemFactory testSystemFactory, Integer iterations, TreeMap<String, File> datasetMap) {

        List<DatasetLoadResult> datasetLoadResults = new ArrayList<>();
        File resultsFolder = testSystemFactory.getResultsFolder();
        String testSystemName = testSystemFactory.getTestSystemName();
        for (int i = 1; i <= iterations; i++) {
            LOGGER.info("------Dataset Load Run- System: {}, Iteration: {} - Started------", testSystemName, i);
            boolean isClear = testSystemFactory.clearDataset();
            if (isClear) {
                DatasetLoadResult datasetLoadResult = testSystemFactory.loadDataset(datasetMap, i);
                datasetLoadResults.add(datasetLoadResult);
            } else {
                LOGGER.error("------Dataset Load Run- System: {}, Iteration: {} - Did not clear.------", testSystemName, i);
            }
            LOGGER.info("------Dataset Load Run- System: {}, Iteration: {} - Completed------", testSystemName, i);
        }

        DatasetLoadResult.writeResultsFile(resultsFolder, datasetLoadResults);

        return datasetLoadResults;
    }

}
