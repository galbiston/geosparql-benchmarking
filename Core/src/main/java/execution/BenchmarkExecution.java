package execution;

import execution_results.DatasetLoadResult;
import execution_results.IterationResult;
import execution_results.QueryResult;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final File RESULTS_FOLDER = new File("../results");

    public static enum BenchmarkType {
        BOTH, WARM, COLD
    }

    static {
        RESULTS_FOLDER.mkdir();
    }

    public static final String COLD_RUN_RESULTS_FOLDER_NAME = "cold_run";
    public static final String WARM_RUN_RESULTS_FOLDER_NAME = "warm_run";

    public static void runType(TestSystemFactory testSystemFactory, ExecutionParameters parameters) {
        runType(testSystemFactory, parameters.getIterations(), parameters.getTimeout(), parameters.getQueryCases(), parameters.getLineLimit(), parameters.getBenchmarkType());

    }

    /**
     *
     * @param testSystemFactory
     * @param iterations
     * @param timeout
     * @param queryCases
     * @param resultsLineLimit Set to zero for no detailed results output.
     * @param benchmarkType Choose both, warm or cold.
     */
    public static void runType(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, List<QueryCase> queryCases, Integer resultsLineLimit, BenchmarkType benchmarkType) {
        switch (benchmarkType) {
            case BOTH:
                runBoth(testSystemFactory, iterations, timeout, queryCases, resultsLineLimit);
                break;
            case WARM:
                runWarm(testSystemFactory, iterations, timeout, queryCases, resultsLineLimit);
                break;
            case COLD:
                runCold(testSystemFactory, iterations, timeout, queryCases, resultsLineLimit);
                break;
        }
    }

    /**
     *
     * @param testSystemFactory
     * @param iterations
     * @param timeout
     * @param queryCases
     * @param resultsLineLimit Set to zero for no detailed results output.
     */
    public static void runBoth(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, List<QueryCase> queryCases, Integer resultsLineLimit) {
        BenchmarkExecution.runCold(testSystemFactory, iterations, timeout, queryCases, resultsLineLimit);
        BenchmarkExecution.runWarm(testSystemFactory, iterations, timeout, queryCases, resultsLineLimit);
    }

    /**
     * Iterate through each query for the test system.<br>
     * An new instance of the test system will be obtained for each query.<br>
     * It is initialised, warmed up by a single query execution that is ignored,
     * then run for the number of iterations before being closed.
     *
     * @param testSystemFactory
     * @param iterations
     * @param timeout
     * @param queryCases
     * @param resultsLineLimit Set to zero for no detailed results output.
     */
    public static final void runWarm(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, List<QueryCase> queryCases, Integer resultsLineLimit) {

        String testSystemName = testSystemFactory.getTestSystemName();
        String testTimestamp = LocalDateTime.now().format(IterationResult.FILE_DATE_TIME_FORMAT);
        File testSystemResultsFolder = testSystemFactory.getResultsFolder();
        File runResultsFolder = new File(testSystemResultsFolder, WARM_RUN_RESULTS_FOLDER_NAME);
        runResultsFolder.mkdir();
        LOGGER.info("------Warm Run - System: {}, Folder: {} - Started------", testSystemName, runResultsFolder);

        for (QueryCase queryCase : queryCases) {
            String queryName = queryCase.getQueryName();
            String queryType = queryCase.getQueryType();
            String queryString = queryCase.getQueryString();

            File resultsFolder = new File(runResultsFolder, queryType);

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
                    //Write summary for all queries and iterations performed to a single file. Reduce footprint by writing immediately.
                    IterationResult.writeSummaryFile(runResultsFolder, iterationResult, testSystemName, testTimestamp);

                    if (queryResult.isCompleted()) {
                        //Write results for all iterations for each query to own file.
                        IterationResult.writeResultsFile(resultsFolder, iterationResult, queryResult, testTimestamp, resultsLineLimit);
                    } else {
                        LOGGER.warn("System: {}, Query: {}, Type: {},  Iteration: {} - Did not complete.", testSystemName, queryName, queryType, i);
                        break;
                    }
                }

            } catch (Exception ex) {
                LOGGER.error("Exception: {}", ex.getMessage());
            }
        }

        LOGGER.info("------Warm Run - System: {}, Folder: {} - Completed------", testSystemName, runResultsFolder);

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
     * @param queryCases
     * @param resultsLineLimit Set to zero for no detailed results output.
     */
    public static final void runCold(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, List<QueryCase> queryCases, Integer resultsLineLimit) {

        String testSystemName = testSystemFactory.getTestSystemName();
        String testTimestamp = LocalDateTime.now().format(IterationResult.FILE_DATE_TIME_FORMAT);
        File testSystemResultsFolder = testSystemFactory.getResultsFolder();
        File runResultsFolder = new File(testSystemResultsFolder, COLD_RUN_RESULTS_FOLDER_NAME);
        runResultsFolder.mkdir();
        LOGGER.info("------Cold Run - System: {}, Folder: {} - Started------", testSystemName, runResultsFolder);

        for (QueryCase queryCase : queryCases) {
            String queryName = queryCase.getQueryName();
            String queryType = queryCase.getQueryType();
            String queryString = queryCase.getQueryString();
            File resultsFolder = new File(runResultsFolder, queryType);

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
                    //Write summary for all queries and iterations performed to a single file. Reduce footprint by writing immediately.
                    IterationResult.writeSummaryFile(runResultsFolder, iterationResult, testSystemName, testTimestamp);

                    if (queryResult.isCompleted()) {
                        //Write results for all iterations for each query to own file.
                        IterationResult.writeResultsFile(resultsFolder, iterationResult, queryResult, testTimestamp, resultsLineLimit);
                    } else {
                        LOGGER.warn("System: {}, Query: {}, Type: {}, Iteration: {} - Did not complete.", testSystemName, queryName, queryType, i);
                        break;
                    }
                } catch (Exception ex) {
                    LOGGER.error("Exception: {}", ex.getMessage());
                }
            }
        }

        LOGGER.info("------Cold Run - System: {}, Folder: {} - Completed------", testSystemName, runResultsFolder);
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

        DatasetLoadResult.writeSummaryFile(resultsFolder, datasetLoadResults);

        return datasetLoadResults;
    }

}
