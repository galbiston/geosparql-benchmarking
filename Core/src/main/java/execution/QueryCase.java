/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
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
        this.queryStrings = Arrays.asList(queryString);
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
            writer.write(queryCase.queryName + " - " + queryCase.queryType);
            writer.newLine();
            writer.newLine();

            int i = 1;
            for (String queryString : queryCase.queryStrings) {
                if (queryCase.queryStrings.size() > 1) {
                    writer.write("Iteration: " + i);
                    writer.newLine();
                }
                i++;
                writer.write(queryString);
                writer.newLine();
            }
            writer.newLine();

        } catch (IOException ex) {
            LOGGER.error("IOException: {}", ex.getMessage());
        }

    }

}
