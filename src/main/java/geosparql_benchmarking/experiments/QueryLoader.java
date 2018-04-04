/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geosparql_benchmarking.experiments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class QueryLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryLoader.class);

    public static HashMap<String, String> loadMapSearchAndBrowsingQueries() {

        HashMap<String, String> queryMap = new HashMap<>();

    }

    public static String readQueryFile(String queryFilename) {

        InputStream input = QueryLoader.class.getResourceAsStream(queryFilename);

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (NullPointerException | IOException ex) {
            LOGGER.error("Could not open query file: {}", queryFilename);
            return null;
        }
    }

}
