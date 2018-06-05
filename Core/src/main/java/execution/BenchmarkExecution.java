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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final File RESULTS_FOLDER = new File("../results");

    public static enum BenchmarkType {
        BOTH, WARM, COLD, CONFORMANCE, DATALOAD
    }

    static {
        RESULTS_FOLDER.mkdir();
    }

    public static final String COLD_RUN_RESULTS_FOLDER_NAME = "cold_run";
    public static final String WARM_RUN_RESULTS_FOLDER_NAME = "warm_run";
    public static final String CONFORMANCE_RUN_RESULTS_FOLDER_NAME = "conformance_run";

    /**
     *
     * @param testSystemFactory
     * @param parameters
     */
    public static void runType(TestSystemFactory testSystemFactory, ExecutionParameters parameters) {
        runType(testSystemFactory, parameters.getIterations(), parameters.getTimeout(), parameters.getQueryCases(), parameters.getLineLimit(), parameters.getBenchmarkType(), parameters.getDatasetMap());
    }

    /**
     *
     * @param testSystemFactory
     * @param iterations
     * @param timeout
     * @param queryCases
     * @param resultsLineLimit Set to zero for no detailed results output.
     * @param benchmarkType Choose both, warm or cold.
     * @param datasetMap
     */
    public static void runType(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, List<QueryCase> queryCases, Integer resultsLineLimit, BenchmarkType benchmarkType, TreeMap<String, File> datasetMap) {
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
            case CONFORMANCE:
                if (iterations > 1) {
                    LOGGER.warn("Conformance Run: Single iteration only of each query on same test system.");
                }
                runConformance(testSystemFactory, timeout, queryCases, resultsLineLimit);
                break;
            case DATALOAD:
                runDatasetLoad(testSystemFactory, iterations, datasetMap);
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

            File resultsFolder = new File(runResultsFolder, queryType);
            QueryCase.writeQueryFile(runResultsFolder, queryCase, testSystemName, testTimestamp);
            long initStartNanoTime = System.nanoTime();
            try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
                long initEndNanoTime = System.nanoTime();
                String queryString = testSystem.translateQuery(queryCase.getQueryString());
                //Warm Up execution.
                LOGGER.info("------Warmup Query - System: {}, Query: {}, Type: {} - Started------", testSystemName, queryName, queryType);
                QueryResult queryResult = runQueryWithTimeout(testSystem, queryString, timeout);
                LOGGER.info("------Warmup Query - System: {}, Query: {}, Type: {} - Completed------", testSystemName, queryName, queryType);

                if (!queryResult.isCompleted()) {
                    LOGGER.error("System: {}, Query: {}, Type: {},  Warm Up - Did not complete.", testSystemName, queryName, queryType);
                } else {
                    //Benchmark executions.
                    for (int i = 0; i < iterations; i++) {
                        queryString = testSystem.translateQuery(queryCase.getQueryString(i));
                        int iteration = i + 1;
                        LOGGER.info("------Query Iteration - System: {}, Query: {}, Type: {}, Iteration: {} - Started------", testSystemName, queryName, queryType, iteration);
                        queryResult = runQueryWithTimeout(testSystem, queryString, timeout);
                        LOGGER.info("------Query Iteration - System: {}, Query: {}, Type: {}, Iteration: {} - Completed------", testSystemName, queryName, queryType, iteration);

                        IterationResult iterationResult = new IterationResult(testSystemName, queryType, queryName, queryString, iteration, queryResult, initStartNanoTime, initEndNanoTime);
                        //Write summary for all queries and iterations performed to a single file. Reduce footprint by writing immediately.
                        IterationResult.writeSummaryFile(runResultsFolder, iterationResult, testSystemName, testTimestamp);

                        if (queryResult.isCompleted()) {
                            //Write results for all iterations for each query to own file.
                            IterationResult.writeResultsFile(resultsFolder, iterationResult, queryResult, testTimestamp, resultsLineLimit);
                        } else {
                            LOGGER.error("System: {}, Query: {}, Type: {},  Iteration: {} - Did not complete.", testSystemName, queryName, queryType, iteration);
                            break;
                        }
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
            File resultsFolder = new File(runResultsFolder, queryType);
            QueryCase.writeQueryFile(runResultsFolder, queryCase, testSystemName, testTimestamp);

            //Benchmark executions.
            for (int i = 0; i < iterations; i++) {
                long initStartNanoTime = System.nanoTime();
                try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
                    long initEndNanoTime = System.nanoTime();
                    String queryString = testSystem.translateQuery(queryCase.getQueryString(i));
                    int iteration = i + 1;
                    LOGGER.info("------Cold Iteration - System: {}, Query: {}, Type: {}, Iteration: {} - Started------", testSystemName, queryName, queryType, iteration);
                    QueryResult queryResult = runQueryWithTimeout(testSystem, queryString, timeout);
                    LOGGER.info("------Cold Iteration - System: {}, Query: {}, Type: {}, Iteration: {} - Completed------", testSystemName, queryName, queryType, iteration);

                    IterationResult iterationResult = new IterationResult(testSystemName, queryType, queryName, queryString, iteration, queryResult, initStartNanoTime, initEndNanoTime);
                    //Write summary for all queries and iterations performed to a single file. Reduce footprint by writing immediately.
                    IterationResult.writeSummaryFile(runResultsFolder, iterationResult, testSystemName, testTimestamp);

                    if (queryResult.isCompleted()) {
                        //Write results for all iterations for each query to own file.
                        IterationResult.writeResultsFile(resultsFolder, iterationResult, queryResult, testTimestamp, resultsLineLimit);
                    } else {
                        LOGGER.warn("System: {}, Query: {}, Type: {}, Iteration: {} - Did not complete.", testSystemName, queryName, queryType, iteration);
                        break;
                    }
                } catch (Exception ex) {
                    LOGGER.error("Exception: {}", ex.getMessage());
                }
            }
        }

        LOGGER.info("------Cold Run - System: {}, Folder: {} - Completed------", testSystemName, runResultsFolder);
    }

    /**
     * Iterate through each query on a single test system.<br>
     * One instance of the test system will be obtained and is re-used for each
     * query. Only one iteration will be performed for each query.
     *
     * @param testSystemFactory
     * @param timeout
     * @param queryCases
     * @param resultsLineLimit Set to zero for no detailed results output.
     */
    public static final void runConformance(TestSystemFactory testSystemFactory, Duration timeout, List<QueryCase> queryCases, Integer resultsLineLimit) {

        String testSystemName = testSystemFactory.getTestSystemName();
        String testTimestamp = LocalDateTime.now().format(IterationResult.FILE_DATE_TIME_FORMAT);
        File testSystemResultsFolder = testSystemFactory.getResultsFolder();
        File runResultsFolder = new File(testSystemResultsFolder, CONFORMANCE_RUN_RESULTS_FOLDER_NAME);
        runResultsFolder.mkdir();
        LOGGER.info("------Conformance Run - System: {}, Folder: {} - Started------", testSystemName, runResultsFolder);
        long initStartNanoTime = System.nanoTime();
        try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
            long initEndNanoTime = System.nanoTime();
            for (QueryCase queryCase : queryCases) {
                String queryName = queryCase.getQueryName();
                String queryType = queryCase.getQueryType();
                File resultsFolder = new File(runResultsFolder, queryType);
                QueryCase.writeQueryFile(runResultsFolder, queryCase, testSystemName, testTimestamp);

                //Benchmark executions.
                String queryString = testSystem.translateQuery(queryCase.getQueryString());

                LOGGER.info("------Conformance Iteration - System: {}, Query: {}, Type: {} - Started------", testSystemName, queryName, queryType);
                QueryResult queryResult = runQueryWithTimeout(testSystem, queryString, timeout);
                LOGGER.info("------Conformance Iteration - System: {}, Query: {}, Type: {} - Completed------", testSystemName, queryName, queryType);

                int iteration = 1;
                IterationResult iterationResult = new IterationResult(testSystemName, queryType, queryName, queryString, iteration, queryResult, initStartNanoTime, initEndNanoTime);
                //Write summary for all queries and iterations performed to a single file. Reduce footprint by writing immediately.
                IterationResult.writeSummaryFile(runResultsFolder, iterationResult, testSystemName, testTimestamp);

                if (queryResult.isCompleted()) {
                    //Write results for all iterations for each query to own file.
                    IterationResult.writeResultsFile(resultsFolder, iterationResult, queryResult, testTimestamp, resultsLineLimit);
                } else {
                    LOGGER.warn("System: {}, Query: {}, Type: {} - Did not complete.", testSystemName, queryName, queryType);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex.getMessage());
        }

        LOGGER.info("------Conformance Run - System: {}, Folder: {} - Completed------", testSystemName, runResultsFolder);
    }

    public static final void runDatasetLoad(TestSystemFactory testSystemFactory, Integer iterations, TreeMap<String, File> datasetMap) {

        String testSystemName = testSystemFactory.getTestSystemName();
        String testTimestamp = LocalDateTime.now().format(DatasetLoadResult.FILE_DATE_TIME_FORMAT);
        List<DatasetLoadResult> datasetLoadResults = new ArrayList<>();
        File resultsFolder = testSystemFactory.getResultsFolder();

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

        DatasetLoadResult.writeSummaryFile(resultsFolder, datasetLoadResults, testSystemName, testTimestamp);
    }

    public static final QueryResult runQueryWithTimeout(TestSystem testSystem, String query, Duration timeout) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(1);
        QueryTask runnable = testSystem.getQueryTask(query);
        Future<?> future = executor.submit(runnable);

        QueryResult queryResult;
        try {
            LOGGER.debug("Query Future: Started");
            future.get(timeout.getSeconds(), TimeUnit.SECONDS);
            queryResult = runnable.getQueryResult();
            LOGGER.debug("Query Future: Completed");
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            LOGGER.error("Query Exception: {}", ex.getMessage());
            queryResult = new QueryResult(0, 0, 0, new ArrayList<>(), false);
        } finally {
            LOGGER.debug("Query Future: Executor Shutdown");
            executor.shutdown();
        }

        return queryResult;
    }

}
