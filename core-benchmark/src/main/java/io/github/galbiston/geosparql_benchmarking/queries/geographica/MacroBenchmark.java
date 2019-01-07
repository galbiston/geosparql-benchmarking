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
package io.github.galbiston.geosparql_benchmarking.queries.geographica;

import io.github.galbiston.geosparql_benchmarking.execution.QueryCase;
import io.github.galbiston.geosparql_benchmarking.execution.QueryLoader;
import io.github.galbiston.geosparql_benchmarking.execution.QueryPair;
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

    public static final String PUBLISHED_FOLDER = "../queries/geographica_benchmarking/macro_benchmark_queries";
    public static final String UNION_FOLDER = "../queries/geographica_benchmarking_union/macro_benchmark_queries";
    private static final Random RANDOM = new Random();

    //Query Folders
    private static final String REVERSE_GEOCODING = "/reverse_geocoding";
    private static final String RAPID_MAPPING = "/rapid_mapping";
    private static final String MAP_SEARCH_AND_BROWSING = "/map_search_and_browsing";

    public static final List<QueryCase> loadQueryCases(String arg, int iterations) {
        switch (arg) {
            case "macro":
                LOGGER.info("Query Set: Geographica Macrobenchmark.");
                return loadAll(iterations, QueryFormat.PUBLISHED);
            case "macro_map_search_and_browsing":
                LOGGER.info("Query Set: Geographica Macrobenchmark Map Search and Browsing.");
                return loadMapSearchAndBrowsingQueries(iterations, QueryFormat.PUBLISHED);
            case "macro_rapid_mapping":
                LOGGER.info("Query Set: Geographica Macrobenchmark Rapid Mapping.");
                return loadRapidMappingQueries(iterations, QueryFormat.PUBLISHED);
            case "macro_reverse_geocoding":
                LOGGER.info("Query Set: Geographica Macrobenchmark Reverse Geocoding.");
                return loadReverseGeocodingQueries(iterations, QueryFormat.PUBLISHED);
            case "macro_union":
                LOGGER.info("Query Set: Geographica Macrobenchmark.");
                return loadAll(iterations, QueryFormat.UNION);
            case "macro_map_search_and_browsing_union":
                LOGGER.info("Query Set: Geographica Macrobenchmark Map Search and Browsing.");
                return loadMapSearchAndBrowsingQueries(iterations, QueryFormat.UNION);
            case "macro_rapid_mapping_union":
                LOGGER.info("Query Set: Geographica Macrobenchmark Rapid Mapping.");
                return loadRapidMappingQueries(iterations, QueryFormat.UNION);
            case "macro_reverse_geocoding_union":
                LOGGER.info("Query Set: Geographica Macrobenchmark Reverse Geocoding.");
                return loadReverseGeocodingQueries(iterations, QueryFormat.UNION);
            default:
                LOGGER.error("Query Set: unrecognised option - {}", arg);
                return null;
        }
    }

    public static final String selectQueryFolder(QueryFormat queryFormat, String subFolder) {
        switch (queryFormat) {
            case UNION:
                return UNION_FOLDER + subFolder;
            default:
                return PUBLISHED_FOLDER + subFolder;
        }
    }

    public static final List<QueryCase> loadAll(Integer iterations, QueryFormat queryFormat) {

        List<QueryCase> queryCases = new ArrayList<>();
        queryCases.addAll(loadMapSearchAndBrowsingQueries(iterations, queryFormat));
        queryCases.addAll(loadRapidMappingQueries(iterations, queryFormat));
        queryCases.addAll(loadReverseGeocodingQueries(iterations, queryFormat));
        return queryCases;
    }

    public static final List<QueryCase> loadMapSearchAndBrowsingQueries(Integer iterations, QueryFormat queryFormat) {
        String queryFolder = selectQueryFolder(queryFormat, MAP_SEARCH_AND_BROWSING);
        String baseFolder = selectQueryFolder(queryFormat, "");
        List<QueryPair> queryPairs = QueryLoader.readQueryPairs(baseFolder + "/geonames.txt");
        int[] indexes = RANDOM.ints(iterations, 0, queryPairs.size()).toArray();

        List<QueryCase> queryCases = new ArrayList<>();
        List<String> queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query0.spl"), "TOPONYME", "");
        queryCases.add(new QueryCase("MS0", "MapSearchAndBrowsing", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query1.spl"), "", "GIVEN_RECTANGLE_IN_WKT");
        queryCases.add(new QueryCase("MS1", "MapSearchAndBrowsing", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query2.spl"), "", "GIVEN_RECTANGLE_IN_WKT");
        queryCases.add(new QueryCase("MS2", "MapSearchAndBrowsing", queryStrings));
        return queryCases;
    }

    public static final List<QueryCase> loadRapidMappingQueries(Integer iterations, QueryFormat queryFormat) {
        String queryFolder = selectQueryFolder(queryFormat, RAPID_MAPPING);
        String baseFolder = selectQueryFolder(queryFormat, "");
        List<QueryPair> queryPairs = QueryLoader.readQueryPairs(baseFolder + "/timestamps.txt");
        int[] indexes = RANDOM.ints(iterations, 0, queryPairs.size()).toArray();

        List<QueryCase> queryCases = new ArrayList<>();
        List<String> queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query0.spl"), "", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM0", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query1.spl"), "", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM1", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query2.spl"), "", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM2", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query3.spl"), "TIMESTAMP", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM3", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query4.spl"), "TIMESTAMP", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM4", "RapidMapping", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query5.spl"), "", "GIVEN_POLYGON_IN_WKT");
        queryCases.add(new QueryCase("RM5", "RapidMapping", queryStrings));
        return queryCases;
    }

    public static final List<QueryCase> loadReverseGeocodingQueries(Integer iterations, QueryFormat queryFormat) {
        String queryFolder = selectQueryFolder(queryFormat, REVERSE_GEOCODING);
        String baseFolder = selectQueryFolder(queryFormat, "");
        List<QueryPair> queryPairs = QueryLoader.readQueryPairs(baseFolder + "/points.txt");
        int[] indexes = RANDOM.ints(iterations, 0, queryPairs.size()).toArray();

        List<QueryCase> queryCases = new ArrayList<>();
        List<String> queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query0.spl"), "", "GIVEN_POINT_IN_WKT");
        queryCases.add(new QueryCase("RG0", "ReverseGeocoding", queryStrings));
        queryStrings = buildIterations(indexes, queryPairs, QueryLoader.readFile(queryFolder + "/Query1.spl"), "", "GIVEN_POINT_IN_WKT");
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
