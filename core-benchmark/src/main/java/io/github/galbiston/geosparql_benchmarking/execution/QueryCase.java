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
package io.github.galbiston.geosparql_benchmarking.execution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class QueryCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String queryName;
    private final String queryType;
    private final List<String> queryStrings;

    public QueryCase(String queryName, String queryType, String queryString) {
        this.queryName = queryName;
        this.queryType = queryType;
        this.queryStrings = new ArrayList<>();
        this.queryStrings.add(queryString);
    }

    public QueryCase(String queryName, String queryType, List<String> queryStrings) {
        this.queryName = queryName;
        this.queryType = queryType;
        this.queryStrings = queryStrings;
    }

    public String getQueryName() {
        return queryName;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getQueryString() {
        return queryStrings.get(0);
    }

    public void addQueryString(String queryString) {
        queryStrings.add(queryString);
    }

    public String getQueryString(int index) throws Exception {
        if (queryStrings.size() == 1) {
            return queryStrings.get(0);
        } else {
            if (index < queryStrings.size()) {
                return queryStrings.get(index);
            } else {
                throw new Exception("Index greater than query string: " + index + " vs " + queryStrings.size());
            }
        }
    }

    @Override
    public String toString() {
        return "QueryCase{" + "queryName=" + queryName + ", queryType=" + queryType + ", queryStrings=" + queryStrings + '}';
    }

    private static final String PADDING_STRING = "########################################";

    public static final void writeQueryFile(File runResultsFolder, QueryCase queryCase, String testSystemName, String testTimestamp) {

        runResultsFolder.mkdir();
        String filename = "querycases-" + testSystemName + "-" + testTimestamp + ".txt";
        File queryCaseFile = new File(runResultsFolder, filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(queryCaseFile, true))) {

            writer.write(PADDING_STRING);
            writer.newLine();
            writer.write("#" + queryCase.queryName + " - " + queryCase.queryType);
            writer.newLine();
            writer.newLine();

            boolean isMultipleStrings = queryCase.queryStrings.size() > 1;
            int i = 1;
            for (String queryString : queryCase.queryStrings) {
                if (isMultipleStrings) {
                    writer.write("#Iteration: " + i);
                    writer.newLine();
                }
                i++;
                writer.write(queryString);
                writer.newLine();
                writer.newLine();
            }
            writer.newLine();

        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }

    }

}
