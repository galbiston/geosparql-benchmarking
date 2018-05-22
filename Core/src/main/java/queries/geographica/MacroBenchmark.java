/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queries.geographica;

import execution.QueryCase;
import execution.QueryLoader;
import execution.QueryPair;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class MacroBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String BASE_FOLDER = "../queries/geographica_benchmarking/macro_benchmark_queries";
    private static final Random RANDOM = new Random();

    //Query Folders
    private static final String REVERSE_GEOCODING = BASE_FOLDER + "/reverse_geocoding";
    private static final String RAPID_MAPPING = BASE_FOLDER + "/rapid_mapping";
    private static final String MAP_SEARCH_AND_BROWSING = BASE_FOLDER + "/map_search_and_browsing";

    public static final List<QueryCase> loadQueryCases(String arg, int iterations) {
        switch (arg) {
            case "macro":
                LOGGER.info("Query Set: Geographica Macrobenchmark.");
                return loadAll(iterations);
            case "macro_map_search_and_browsing":
                LOGGER.info("Query Set: Geographica Macrobenchmark Map Search and Browsing.");
                return loadMapSearchAndBrowsingQueries(iterations);
            case "macro_rapid_mapping":
                LOGGER.info("Query Set: Geographica Macrobenchmark Rapid Mapping.");
                return loadRapidMappingQueries(iterations);
            case "macro_reverse_geocoding":
                LOGGER.info("Query Set: Geographica Macrobenchmark Reverse Geocoding.");
                return loadReverseGeocodingQueries(iterations);
            default:
                LOGGER.error("Query Set: unrecognised option - {}", arg);
                return null;
        }
    }

    public static final List<QueryCase> loadAll(Integer iterations) {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.addAll(loadMapSearchAndBrowsingQueries(iterations));
        queryCases.addAll(loadRapidMappingQueries(iterations));
        queryCases.addAll(loadReverseGeocodingQueries(iterations));
        return queryCases;
    }

    public static final List<QueryCase> loadMapSearchAndBrowsingQueries(Integer iterations) {
        List<QueryPair> queryPairs = QueryLoader.readQueryPairs(BASE_FOLDER + "/geonames.txt");
        int[] indexes = RANDOM.ints(iterations, 0, queryPairs.size()).toArray();

        List<QueryCase> queryCases = new ArrayList<>();
        List<String> queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(MAP_SEARCH_AND_BROWSING + "/Query0.spl"), "TOPONYME", "");
        queryCases.add(new QueryCase("MS0", "MapSearchAndBrowsing", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(MAP_SEARCH_AND_BROWSING + "/Query1.spl"), "", "GIVEN_RECTANGLE_IN_WKT");
        queryCases.add(new QueryCase("MS1", "MapSearchAndBrowsing", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(MAP_SEARCH_AND_BROWSING + "/Query2.spl"), "", "GIVEN_RECTANGLE_IN_WKT");
        queryCases.add(new QueryCase("MS2", "MapSearchAndBrowsing", queryStrings));
        return queryCases;
    }

    public static final List<QueryCase> loadRapidMappingQueries(Integer iterations) {
        List<QueryPair> queryPairs = QueryLoader.readQueryPairs(BASE_FOLDER + "/timestamps.txt");
        int[] indexes = RANDOM.ints(iterations, 0, queryPairs.size()).toArray();

        List<QueryCase> queryCases = new ArrayList<>();
        List<String> queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(RAPID_MAPPING + "/Query0.spl"), "", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM0", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(RAPID_MAPPING + "/Query1.spl"), "", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM1", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(RAPID_MAPPING + "/Query2.spl"), "", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM2", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(RAPID_MAPPING + "/Query3.spl"), "TIMESTAMP", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM3", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(RAPID_MAPPING + "/Query4.spl"), "TIMESTAMP", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM4", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(RAPID_MAPPING + "/Query5.spl"), "", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM5", "RapidMapping", queryStrings));
        return queryCases;
    }

    public static final List<QueryCase> loadReverseGeocodingQueries(Integer iterations) {
        List<QueryPair> queryPairs = QueryLoader.readQueryPairs(BASE_FOLDER + "/points.txt");
        int[] indexes = RANDOM.ints(iterations, 0, queryPairs.size()).toArray();

        List<QueryCase> queryCases = new ArrayList<>();
        List<String> queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(REVERSE_GEOCODING + "/Query0.spl"), "", "GIVEN_POINT_IN_WKT");
        queryCases.add(new QueryCase("RG0", "ReverseGeocoding", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(REVERSE_GEOCODING + "/Query1.spl"), "", "GIVEN_POINT_IN_WKT");
        queryCases.add(new QueryCase("RG1", "ReverseGeocoding", queryStrings));
        return queryCases;
    }

    public static final List<String> buildIterations(int[] indexes, List<QueryPair> queryPairs, String queryString, String labelMatch, String geometryMatch) {
        List<String> queryStrings = new ArrayList<>(indexes.length);

        for (int index : indexes) {
            String preparedString = queryString;
            QueryPair queryPair = queryPairs.get(index);
            if (!labelMatch.isEmpty()) {
                preparedString = preparedString.replace(labelMatch, queryPair.getLabel());
            }

            if (!geometryMatch.isEmpty()) {
                preparedString = preparedString.replace(geometryMatch, queryPair.getGeometry());
            }

            queryStrings.add(preparedString);
        }
        return queryStrings;
    }

}
