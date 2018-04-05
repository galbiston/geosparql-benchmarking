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
    private final String queryName;
    private final String queryString;
    private final String iteration;
    private final QueryResult queryResult;

    public IterationResult(String testSystemName, String queryName, String queryString, Integer iteration, QueryResult queryResult) {
        this.testSystemName = testSystemName;
        this.queryName = queryName;
        this.queryString = queryString;
        this.iteration = iteration.toString();
        this.queryResult = queryResult;
    }

    public String getTestSystemName() {
        return testSystemName;
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
        return testSystemName + "-" + queryName;
    }

    @Override
    public String toString() {
        return "IterationResult{" + "testSystemName=" + testSystemName + ", queryName=" + queryName + ", queryString=" + queryString + ", iteration=" + iteration + ", queryResult=" + queryResult + '}';
    }

    public static final String[] SUMMARY_HEADER = {"TestSystem", "QueryName", "Iteration", "Completed", "ResultsCount", "StartQueryDuration", "QueryResultsDuration", "StartResultsDuration"};

    public String[] writeSummary() {
        List<String> line = new ArrayList<>(SUMMARY_HEADER.length);
        line.add(testSystemName);
        line.add(queryName);
        line.add(iteration);
        line.add(queryResult.isCompleted().toString());
        line.add(queryResult.getResultsCount().toString());
        line.add(queryResult.getStartQueryDuration().toString());
        line.add(queryResult.getQueryResultsDuration().toString());
        line.add(queryResult.getStartResultsDuration().toString());
        return line.toArray(new String[line.size()]);
    }

    public static final void writeSummaryFile(File resultsFolder, List<IterationResult> allIterationResults) {

        File summaryFile = new File(resultsFolder, "summary.csv");
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

            List<String> line = new ArrayList<>(3 + resultsLabels.size());
            line.add(testSystemName);
            line.add(queryName);
            line.add(iteration);
            for (String label : resultsLabels) {
                if (result.containsKey(label)) {
                    String value = result.get(label);
                    line.add(value);
                } else {
                    LOGGER.error("System: {}, Query: {}, Iteration: {} - Query Result does not contain expected label {}. Only different iterations of the same query should be used to write results file.", testSystemName, queryName, iteration, label);
                }
            }

            lines.add(line.toArray(new String[line.size()]));
        }
        return lines;
    }

    public static final void writeResultsFile(File resultsFolder, List<IterationResult> iterationResults) {

        if (!iterationResults.isEmpty()) {
            IterationResult firstIterationResult = iterationResults.get(0);
            File resultsFile = new File(resultsFolder, firstIterationResult.getResultFileLabel() + "-results.csv");
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
