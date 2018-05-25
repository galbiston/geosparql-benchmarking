/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class QueryLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryLoader.class);

    public static final String readFile(String filepath) {

        File file = new File(filepath);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (NullPointerException | IOException ex) {
            LOGGER.error("Could not open query file: {}#{}", file, ex.getMessage());
            return null;
        }
    }

    public static final List<QueryCase> readQuery(String filepath) {
        return Arrays.asList(readQuery(filepath, 1));
    }

    public static final QueryCase readQuery(String filepath, int count) {

        String[] fileName = new File(filepath).getName().split("-");
        String queryName;
        if (fileName.length > 1) {
            queryName = fileName[0];
        } else {
            queryName = "UQ" + count;
        }

        return new QueryCase(queryName, "UserQuery", readFile(filepath));
    }

    public static final List<QueryCase> readFolder(File directory) {

        List<QueryCase> queryCases = new ArrayList<>();
        File[] files = directory.listFiles();
        int count = 0;
        for (File file : files) {
            count++;
            String filepath = file.getAbsolutePath();
            QueryCase queryCase = readQuery(filepath, count);
            checkIteration(queryCases, queryCase);
        }
        return queryCases;
    }

    private static void checkIteration(List<QueryCase> queryCases, QueryCase queryCase) {

        boolean isNoMatch = true;
        for (QueryCase existingCase : queryCases) {

            String queryName = existingCase.getQueryName();
            if (queryName.equals(queryCase.getQueryName())) {
                existingCase.addQueryString(queryCase.getQueryString());
                isNoMatch = false;
                break;
            }
        }

        if (isNoMatch) {
            queryCases.add(queryCase);
        }
    }

    public static final List<QueryPair> readQueryPairs(String filepath) {

        List<QueryPair> queryPairs = new ArrayList<>();
        File file = new File(filepath);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            while (buffer.ready()) {
                String line = buffer.readLine();
                String[] parts = line.split("\t");
                QueryPair queryPair = new QueryPair(parts[0], parts[1]);
                queryPairs.add(queryPair);
            }

        } catch (NullPointerException | IOException ex) {
            LOGGER.error("Could not open query file: {}#{}", file, ex.getMessage());
            return null;
        }

        return queryPairs;
    }

}
