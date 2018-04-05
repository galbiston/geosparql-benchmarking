/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class IterationResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String testSystemName;
    private final String queryType;
    private final String queryName;
    private final String queryString;
    private final String iteration;
    private final QueryResult queryResult;

    public IterationResult(String testSystemName, String queryType, String queryName, String queryString, Integer iteration, QueryResult queryResult) {
        this.testSystemName = testSystemName;
        this.queryType = queryType;
        this.queryName = queryName;
        this.queryString = queryString;
        this.iteration = iteration.toString();
        this.queryResult = queryResult;
    }

    public String getTestSystemName() {
        return testSystemName;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getQueryName() {
        return queryName;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getIteration() {
        return iteration;
    }

    public QueryResult getQueryResult() {
        return queryResult;
    }

    public String getResultFileLabel() {
        return testSystemName + "-" + queryType + "-" + queryName;
    }

    @Override
    public String toString() {
        return "IterationResult{" + "testSystemName=" + testSystemName + ", queryType=" + queryType + ", queryName=" + queryName + ", queryString=" + queryString + ", iteration=" + iteration + ", queryResult=" + queryResult + '}';
    }

    public static final String[] SUMMARY_HEADER = {"TestSystem", "QueryType", "QueryName", "Iteration", "Completed", "ResultsCount", "StartQueryDuration", "QueryResultsDuration", "StartResultsDuration"};

    public String[] writeSummary() {
        List<String> line = new ArrayList<>(SUMMARY_HEADER.length);
        line.add(testSystemName);
        line.add(queryType);
        line.add(queryName);
        line.add(iteration);
        line.add(queryResult.isCompleted().toString());
        line.add(queryResult.getResultsCount().toString());
        line.add(queryResult.getStartQueryDuration().toString());
        line.add(queryResult.getQueryResultsDuration().toString());
        line.add(queryResult.getStartResultsDuration().toString());
        return line.toArray(new String[line.size()]);
    }

    public static final void writeSummaryFile(File systemResultsFolder, List<IterationResult> allIterationResults) {

        String filename = "summary-" + LocalDateTime.now() + ".csv";
        File summaryFile = new File(systemResultsFolder, filename);
        try (CSVWriter writer = new CSVWriter(new FileWriter(summaryFile))) {
            writer.writeNext(SUMMARY_HEADER);
            for (IterationResult iterationResult : allIterationResults) {
                writer.writeNext(iterationResult.writeSummary());
            }

        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }

    }

    public final String[] getResultHeader() {
        List<String> header = new ArrayList<>(3 + queryResult.getResultsVariableCount());
        header.add("TestSystem");
        header.add("QueryName");
        header.add("Iteration");
        header.addAll(queryResult.getResultsVariableLabels());
        return header.toArray(new String[header.size()]);
    }

    public final List<String[]> writeResults() {

        List<String[]> lines = new ArrayList<>();
        List<String> resultsLabels = queryResult.getResultsVariableLabels();
        for (HashMap<String, String> result : queryResult.getResults()) {

            List<String> line = new ArrayList<>(4 + resultsLabels.size());
            line.add(testSystemName);
            line.add(queryType);
            line.add(queryName);
            line.add(iteration);
            for (String label : resultsLabels) {
                if (result.containsKey(label)) {
                    String value = result.get(label);
                    line.add(value);
                } else {
                    LOGGER.error("System: {}, Type: {}, Query: {}, Iteration: {} - Query Result does not contain expected label {}. Only different iterations of the same query should be used to write results file.", testSystemName, queryType, queryName, iteration, label);
                }
            }

            lines.add(line.toArray(new String[line.size()]));
        }
        return lines;
    }

    public static final void writeResultsFile(File resultsFolder, List<IterationResult> iterationResults) {

        if (!iterationResults.isEmpty()) {
            IterationResult firstIterationResult = iterationResults.get(0);
            String filename = firstIterationResult.getResultFileLabel() + "-results-" + LocalDateTime.now() + " .csv";
            File resultsFile = new File(resultsFolder, filename);
            try (CSVWriter writer = new CSVWriter(new FileWriter(resultsFile))) {
                writer.writeNext(firstIterationResult.getResultHeader());
                for (IterationResult iterationResult : iterationResults) {
                    writer.writeAll(iterationResult.writeResults());
                }

            } catch (IOException ex) {
                LOGGER.error("IOException: {}", ex.getMessage());
            }
        } else {
            LOGGER.warn("Iteration Results is Empty - Not writing to {}.", resultsFolder);
        }
    }

}
