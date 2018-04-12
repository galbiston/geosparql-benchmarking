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
import java.time.Duration;
import java.time.format.DateTimeFormatter;
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
    private final String resultsFileLabel;
    private final String iteration;
    private final Boolean isQueryComplete;
    private final Integer queryResultsCount;
    private final Duration startQueryDuration;
    private final Duration queryResultsDuration;
    private final Duration startResultsDuration;
    private final String nonEmptyResultsCount;
    private final long initStartNanoTime;
    private final long initEndNanoTime;
    private final Duration initStartEndDuration;

    public IterationResult(String testSystemName, String queryType, String queryName, String queryString, Integer iteration, QueryResult queryResult, long initStartNanoTime, long initEndNanoTime) {
        this.testSystemName = testSystemName;
        this.queryType = queryType;
        this.queryName = queryName;
        this.queryString = queryString;
        this.iteration = iteration.toString();
        this.isQueryComplete = queryResult.isCompleted();
        this.queryResultsCount = queryResult.getResultsCount();
        this.startQueryDuration = queryResult.getStartQueryDuration();
        this.queryResultsDuration = queryResult.getQueryResultsDuration();
        this.startResultsDuration = queryResult.getStartResultsDuration();
        this.resultsFileLabel = testSystemName + "-" + queryType + "-" + queryName + "-" + iteration;

        Integer nonEmptyCount = 0;
        for (HashMap<String, String> result : queryResult.getResults()) {
            if (!result.isEmpty()) {
                nonEmptyCount++;
            }
        }
        this.nonEmptyResultsCount = nonEmptyCount.toString();
        this.initStartNanoTime = initStartNanoTime;
        this.initEndNanoTime = initEndNanoTime;
        this.initStartEndDuration = Duration.ofNanos(initEndNanoTime - initStartNanoTime);
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

    public Boolean getIsQueryComplete() {
        return isQueryComplete;
    }

    public Integer getQueryResultsCount() {
        return queryResultsCount;
    }

    public Duration getStartQueryDuration() {
        return startQueryDuration;
    }

    public Duration getQueryResultsDuration() {
        return queryResultsDuration;
    }

    public Duration getStartResultsDuration() {
        return startResultsDuration;
    }

    public String getNonEmptyResultsCount() {
        return nonEmptyResultsCount;
    }

    public String getResultFileLabel() {
        return resultsFileLabel;
    }

    public long getInitStartNanoTime() {
        return initStartNanoTime;
    }

    public long getInitEndNanoTime() {
        return initEndNanoTime;
    }

    public Duration getInitStartEndDuration() {
        return initStartEndDuration;
    }

    @Override
    public String toString() {
        return "IterationResult{" + "testSystemName=" + testSystemName + ", queryType=" + queryType + ", queryName=" + queryName + ", queryString=" + queryString + ", iteration=" + iteration + ", isQueryComplete=" + isQueryComplete + ", queryResultsCount=" + queryResultsCount + ", startQueryDuration=" + startQueryDuration + ", queryResultsDuration=" + queryResultsDuration + ", startResultsDuration=" + startResultsDuration + ", nonEmptyResultsCount=" + nonEmptyResultsCount + ", initStartNanoTime=" + initStartNanoTime + ", initEndNanoTime=" + initEndNanoTime + ", initStartEndDuration=" + initStartEndDuration + '}';
    }

    public static final String[] SUMMARY_HEADER = {"TestSystem", "QueryType", "QueryName", "Iteration", "Completed", "ResultsCount", "NonEmptyResultsCount", "InitStartEndDuration", "StartQueryDuration", "QueryResultsDuration", "StartResultsDuration"};

    public String[] writeSummary() {
        List<String> line = new ArrayList<>(SUMMARY_HEADER.length);
        line.add(testSystemName);
        line.add(queryType);
        line.add(queryName);
        line.add(iteration);
        line.add(isQueryComplete.toString());
        line.add(queryResultsCount.toString());
        line.add(nonEmptyResultsCount);
        line.add(initStartEndDuration.toString());
        line.add(startQueryDuration.toString());
        line.add(queryResultsDuration.toString());
        line.add(startResultsDuration.toString());
        return line.toArray(new String[line.size()]);
    }

    public static final DateTimeFormatter FILE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public static final void writeSummaryFile(File systemResultsFolder, List<IterationResult> allIterationResults, String testTimestamp) {

        String filename = "summary-" + testTimestamp + ".csv";
        File summaryFile = new File(systemResultsFolder, filename);
        boolean summaryFileAlreadyExists = summaryFile.exists();

        try (CSVWriter writer = new CSVWriter(new FileWriter(summaryFile, true))) {
            if (!summaryFileAlreadyExists) {
                writer.writeNext(SUMMARY_HEADER);
            }
            for (IterationResult iterationResult : allIterationResults) {
                writer.writeNext(iterationResult.writeSummary());
            }

        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }

    }

    public static final String[] getResultHeader(QueryResult queryResult) {
        List<String> header = new ArrayList<>(4 + queryResult.getResultsVariableCount());
        header.add("TestSystem");
        header.add("QueryType");
        header.add("QueryName");
        header.add("Iteration");
        header.addAll(queryResult.getResultsVariableLabels());
        return header.toArray(new String[header.size()]);
    }

    public static final List<String[]> writeResults(IterationResult iterationResult, QueryResult queryResult) {

        List<String[]> lines = new ArrayList<>();
        List<String> resultsLabels = queryResult.getResultsVariableLabels();
        for (HashMap<String, String> result : queryResult.getResults()) {

            List<String> line = new ArrayList<>(4 + resultsLabels.size());
            line.add(iterationResult.testSystemName);
            line.add(iterationResult.queryType);
            line.add(iterationResult.queryName);
            line.add(iterationResult.iteration);
            for (String label : resultsLabels) {
                if (result.containsKey(label)) {
                    String value = result.get(label);
                    line.add(value);
                } else {
                    LOGGER.error("System: {}, Type: {}, Query: {}, Iteration: {} - Query Result does not contain expected label {}. Only different iterations of the same query should be used to write results file.", iterationResult.testSystemName, iterationResult.queryType, iterationResult.queryName, iterationResult.iteration, label);
                }
            }

            lines.add(line.toArray(new String[line.size()]));
        }
        return lines;
    }

    public static final void writeResultsFile(File resultsFolder, IterationResult iterationResult, QueryResult queryResult, String testTimestamp) {

        String filename = iterationResult.getResultFileLabel() + "-results-" + testTimestamp + ".csv";
        File resultsFile = new File(resultsFolder, filename);
        try (CSVWriter writer = new CSVWriter(new FileWriter(resultsFile))) {
            writer.writeNext(IterationResult.getResultHeader(queryResult));
            writer.writeAll(IterationResult.writeResults(iterationResult, queryResult));
        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }
    }

}
