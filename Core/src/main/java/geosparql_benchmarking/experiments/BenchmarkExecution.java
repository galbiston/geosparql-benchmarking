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

    /**
     * Iterate through each query for the test system. An new instance of the
     * test system will be obtained for each query. It is initialised, warmed up
     * a single execution that is ignored, then run for the number of iterations
     * before being closed.
     *
     * @param testSystemFactory
     * @param iterations
     * @param timeout
     * @param queryMap
     * @return
     */
    public static final List<IterationResult> run(TestSystemFactory testSystemFactory, Integer iterations, Duration timeout, HashMap<String, String> queryMap) {

        String testSystemName = testSystemFactory.getTestSystemName();
        File testSystemResultsFolder = testSystemFactory.getResultsFolder();

        LOGGER.info("----------System: {}, Folder: {} - Started----------", testSystemName, testSystemResultsFolder);

        List<IterationResult> allIterationResults = new ArrayList<>(queryMap.size() * iterations);
        for (Entry<String, String> entry : queryMap.entrySet()) {
            List<IterationResult> iterationResults = new ArrayList<>(iterations);
            String[] queryLabel = entry.getKey().split("#");
            String queryType = queryLabel[0];
            String queryName = queryLabel[1];
            File resultsFolder = new File(testSystemResultsFolder, queryType);
            resultsFolder.mkdir();
            String queryString = entry.getValue();

            try (TestSystem testSystem = testSystemFactory.getTestSystem()) {
                queryString = testSystem.translateQuery(queryString);
                //Warm Up execution.
                LOGGER.info("----------System: {}, Type: {}, Query: {}, Warmup - Started----------", testSystemName, queryType, queryName);
                QueryResult queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                LOGGER.info("----------System: {}, Type: {}, Query: {}, Warmup - Completed----------", testSystemName, queryType, queryName);

                if (queryResult.isCompleted()) {
                    //Benchmark executions.
                    for (int i = 1; i <= iterations; i++) {
                        LOGGER.info("----------System: {}, Type: {}, Query: {}, Iteration: {} - Started----------", testSystemName, queryType, queryName, i);
                        queryResult = testSystem.runQueryWithTimeout(queryString, timeout);
                        LOGGER.info("----------System: {}, Type: {}, Query: {}, Iteration: {} - Completed----------", testSystemName, queryType, queryName, i);
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
        IterationResult.writeSummaryFile(testSystemResultsFolder, allIterationResults);
        LOGGER.info("----------System: {}, Folder: {} - Started----------", testSystemFactory.getTestSystemName(), testSystemFactory.getResultsFolder());

        return allIterationResults;
    }
}
