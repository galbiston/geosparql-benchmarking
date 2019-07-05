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
package io.github.galbiston.geosparql_benchmarking.results_validation;

import io.github.galbiston.geosparql_benchmarking.execution.QueryCase;
import static io.github.galbiston.geosparql_benchmarking.execution.QueryLoader.getFileExtensionName;
import static io.github.galbiston.geosparql_benchmarking.execution.QueryLoader.readQueryXMLFile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.CollectionUtils;

public class QueryResultsValidator {

    static List<String> expectedResults = new ArrayList<>();
    static List<String> queryResults = new ArrayList<>();
    static QueryResultType qrValidator;
    private static String folderName = null;
    private static QueryCase queryCase = null;
    private static QueryResultSeverity qrSeverity;

    public static void run(String directory) {
        readFolder(new File(directory));
    }

    public static void validateResults() {
        Boolean passedBln = false;

        try {
            System.out.println("runValidation!");

            /*Passes*/
            /**
             * Pass with results
             */
            if (qrValidator.equals(QueryResultType.PASS) && qrSeverity.equals(QueryResultSeverity.WITH_RESULTS)) {
                if (expectedResults.size() != 0 && queryResults.size() != 0
                        && CollectionUtils.isEqualCollection(expectedResults, queryResults)) {
                    System.out.println("PASS - WITH_RESULTS!");
                    passedBln = true;
                }
            } /**
             * Pass with no results
             */
            else if (qrValidator.equals(QueryResultType.PASS) && qrSeverity.equals(QueryResultSeverity.WITHOUT_RESULTS)) {
                if (expectedResults.size() == 0 && queryResults.size() == 0) {
                    System.out.println("PASS - WITHOUT_RESULTS!");
                    passedBln = true;
                }
            } /**
             * Pass with error
             */
            else if (qrValidator.equals(QueryResultType.PASS) && qrSeverity.equals(QueryResultSeverity.WITH_ERROR)) {
                //TODO: Figure out how to collect the errors - using a custom exception handler?
            } /**
             * Pass with warning
             */
            else if (qrValidator.equals(QueryResultType.PASS) && qrSeverity.equals(QueryResultSeverity.WITH_WARNING)) {
                //TODO: Describe the warnings, Could have results or not
                if (CollectionUtils.isEqualCollection(expectedResults, queryResults)) {
                    System.out.println("PASS - WITH_WARNING!");
                    passedBln = true;
                }
            }

            /*Fails*/
            /**
             * Fail with results
             */
            if (qrValidator.equals(QueryResultType.FAIL) && qrSeverity.equals(QueryResultSeverity.WITH_RESULTS)) {
                if (expectedResults.size() != 0 && queryResults.size() != 0
                        && CollectionUtils.isEqualCollection(expectedResults, queryResults)) {
                    System.out.println("FAIL - WITH_RESULTS!");
                    passedBln = true;
                }
            } /**
             * Fail with no results
             */
            else if (qrValidator.equals(QueryResultType.FAIL) && qrSeverity.equals(QueryResultSeverity.WITHOUT_RESULTS)) {
                if (expectedResults.size() == 0 && queryResults.size() == 0) {
                    System.out.println("FAIL - WITHOUT_RESULTS!");
                    passedBln = true;
                }
            } /**
             * Fail with error
             */
            else if (qrValidator.equals(QueryResultType.FAIL) && qrSeverity.equals(QueryResultSeverity.WITH_ERROR)) {
                //TODO: Figure out how to collect the errors - using a custom exception handler?
            } /**
             * Pass with warning
             */
            else if (qrValidator.equals(QueryResultType.FAIL) && qrSeverity.equals(QueryResultSeverity.WITH_WARNING)) {
                //TODO: Describe the warnings, Could have results or not
                if (CollectionUtils.isEqualCollection(expectedResults, queryResults)) {
                    System.out.println("FAIL - WITH_WARNING!");
                    passedBln = true;
                }
            }

            /*Save results*/
            System.out.println("ExpectedResults:" + expectedResults.toString());
            System.out.println("QueryResults:" + queryResults.toString());
            saveConformanceResult(qrValidator, qrSeverity, passedBln, folderName + "\\" + "conformance_results.txt");
        } catch (IOException ex) {
        }
    }

    public static List<String> sortFile(String inputFile) throws Exception {
        FileReader fileReader = new FileReader(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String inputLine;
        List<String> sortedList = new ArrayList<String>();
        while ((inputLine = bufferedReader.readLine()) != null) {
            sortedList.add(inputLine);
        }
        fileReader.close();

        Collections.sort(sortedList);
        return sortedList;
    }

    public static final List<QueryCase> readFolder(File directory) {
        return readFolder(directory, 0);
    }

    public static final List<QueryCase> readFolder(File directory, int count) {
        List<QueryCase> queryCases = new ArrayList<>();
        File[] files = directory.listFiles();

        for (File file : files) {

            if (file.isDirectory()) {
                List<QueryCase> folderQueryCases = readFolder(file, count);
                count += folderQueryCases.size();
                queryCases.addAll(folderQueryCases);
            } else {
                try {
                    count++;
                    String filepath = file.getAbsolutePath();
                    File pf = new File(filepath);

                    if (pf.isFile() && getFileExtensionName(pf).indexOf("xml") != -1) {
                        queryCase = readQueryXMLFile(filepath, count, directory.getName());
                        qrValidator = queryCase.getQueryResultType().get(0);
                        qrSeverity = queryCase.getQueryResultSeverity().get(0);

                        folderName = pf.getAbsoluteFile().getParent();
                        //System.out.println(folderName);

                        expectedResults = sortFile(folderName + "\\" + "expected_results.txt");
                        queryResults = sortFile(folderName + "\\" + "query_results.txt");

                        validateResults();
                    }
                } catch (Exception ex) {
                    // Logger.getLogger(QueryResultsValidator.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return queryCases;
    }

    /**
     * Using BufferedWriter to reduce real IO operations and saves time
     *
     * @param queryResult
     * @param queryResultFileName
     */
    public static void saveConformanceResult(QueryResultType qrt, QueryResultSeverity qrs, Boolean validationResultBln, String validationResultFileName) throws IOException {

        File file = new File(validationResultFileName);
        FileWriter fr = null;
        BufferedWriter br = null;

        try {
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);

            //QueryType, QueryName, Expected Conformance Result, Expected Query Result Severity, Result
            br.write("QueryType:" + queryCase.getQueryType() + " QueryName:" + queryCase.getQueryName() + " Expected:"
                    + qrt.name() + " - " + qrs.name() + " Result:" + ((validationResultBln) ? "PASSED" : "FAILED"));

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
}
