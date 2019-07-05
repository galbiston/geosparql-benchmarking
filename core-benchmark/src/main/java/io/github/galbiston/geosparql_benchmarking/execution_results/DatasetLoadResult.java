/**
 * Copyright 2018 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.galbiston.geosparql_benchmarking.execution_results;

import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class DatasetLoadResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetLoadResult.class);

    private final String testSystemName;
    private final Boolean isCompleted;
    private final Integer iteration;
    private final long startNanoTime;
    private final long endNanoTime;
    private final Duration startEndDuration;
    private final List<DatasetLoadTimeResult> datasetLoadTimeResults;
    private static Integer DATASET_COUNT = 0;
    private static final String[] SUMMARY_DEFAULT_HEADER = {"TestSystem", "Completed", "Iteration", "StartEndDuration"};
    private static String[] SUMMARY_HEADER = SUMMARY_DEFAULT_HEADER;

    public DatasetLoadResult(String testSystemName, Boolean isCompleted, Integer iteration, long startNanoTime, long endNanoTime, List<DatasetLoadTimeResult> datasetLoadTimeResults) {
        this.testSystemName = testSystemName;
        this.isCompleted = isCompleted;
        this.iteration = iteration;
        this.startNanoTime = startNanoTime;
        this.endNanoTime = endNanoTime;
        this.startEndDuration = Duration.ofNanos(endNanoTime - startNanoTime);
        this.datasetLoadTimeResults = datasetLoadTimeResults;

        if (datasetLoadTimeResults.size() > DATASET_COUNT) {
            //Assuming consistency in Dataset namings.
            DATASET_COUNT = datasetLoadTimeResults.size();
            List<String> datasetNames = new ArrayList<>(DATASET_COUNT);
            for (DatasetLoadTimeResult result : datasetLoadTimeResults) {
                datasetNames.add(result.getDatasetName());
            }
            SUMMARY_HEADER = ArrayUtils.addAll(SUMMARY_DEFAULT_HEADER, datasetNames.toArray(new String[datasetNames.size()]));
        }
    }

    public String getTestSystemName() {
        return testSystemName;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public Integer getIteration() {
        return iteration;
    }

    public long getStartNanoTime() {
        return startNanoTime;
    }

    public long getEndNanoTime() {
        return endNanoTime;
    }

    public Duration getStartEndDuration() {
        return startEndDuration;
    }

    public List<DatasetLoadTimeResult> getDatasetLoadTimeResults() {
        return datasetLoadTimeResults;
    }

    @Override
    public String toString() {
        return "DatasetLoadResult{" + "testSystemName=" + testSystemName + ", isCompleted=" + isCompleted + ", iteration=" + iteration + ", startNanoTime=" + startNanoTime + ", endNanoTime=" + endNanoTime + ", startEndDuration=" + startEndDuration + ", datasetLoadTimeResults=" + datasetLoadTimeResults + '}';
    }

    public String[] writeSummaryLine() {
        List<String> line = new ArrayList<>(SUMMARY_DEFAULT_HEADER.length + DATASET_COUNT);
        line.add(testSystemName);
        line.add(isCompleted.toString());
        line.add(iteration.toString());
        line.add(startEndDuration.toString());

        for (DatasetLoadTimeResult result : datasetLoadTimeResults) {
            line.add(result.getStartEndDuration().toString());
        }
        return line.toArray(new String[line.size()]);
    }

    public static final DateTimeFormatter FILE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public static final void writeSummaryFile(File datasetLoadResultsFolder, DatasetLoadResult datasetLoadResult, String testSystemName, String testTimestamp) {
        DatasetLoadResult.writeSummaryFile(datasetLoadResultsFolder, Arrays.asList(datasetLoadResult), testSystemName, testTimestamp);
    }

    public static final void writeSummaryFile(File datasetLoadResultsFolder, List<DatasetLoadResult> datasetLoadResults, String testSystemName, String testTimestamp) {
        datasetLoadResultsFolder.mkdir();
        String filename = "datasetload-" + testSystemName + "-" + testTimestamp + ".csv";
        File summaryFile = new File(datasetLoadResultsFolder, filename);
        boolean summaryFileAlreadyExists = summaryFile.exists();

        try (CSVWriter writer = new CSVWriter(new FileWriter(summaryFile, true))) {

            if (!summaryFileAlreadyExists) {
                writer.writeNext(SUMMARY_HEADER);
            }
            for (DatasetLoadResult datasetLoadResult : datasetLoadResults) {
                writer.writeNext(datasetLoadResult.writeSummaryLine());
            }

        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }
    }
    
     /**
     * Using BufferedWriter to reduce real IO operations and saves time
     * @param queryResult
     * @param queryResultFileName
     */
    public static void saveQueryResult(QueryResult queryResult, String queryResultFileName) {
        
        File file = new File(queryResultFileName);
        FileWriter fr = null;
        BufferedWriter br = null;

        LOGGER.info("------ Saving Query Results to file:" + queryResult.toString());

        try {
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);

            for (List<VarValue> vvList : queryResult.getResults()) {
                for (VarValue vv : vvList) {
                    //LOGGER.info("VAR:" + vv.toString());
                     br.write(vv.getValue().replace("\"",""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Using BufferedWriter to reduce real IO operations and saves time
     * @param queryResult
     * @param queryResultFileName
     */
    public static void saveQueryResult(String queryResult, String queryResultFileName) {

        File file = new File(queryResultFileName);
        FileWriter fr = null;
        BufferedWriter br = null;

        LOGGER.info("------ Saving Query Results to file:" + queryResult);

        try {
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);

            br.write(queryResult.replace("\"", ""));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void getQueryCaseXMLFiles(String directoryName, List<File> files) {
        File directory = new File(directoryName);

        //System.out.println("FILE:"+directory.getAbsolutePath());
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    String testFiles = "query_results.txt|conformance_results.txt";
                    if (testFiles.toLowerCase().contains(file.getName().toLowerCase())) {
                        System.out.println("DELETE:" + file);
                        file.delete();
                    }
                } else if (file.isDirectory()) {
                    getQueryCaseXMLFiles(file.getAbsolutePath(), files);
                    System.out.println(file);
                    files.add(file);
                }
            }
        }
    }
    
}
